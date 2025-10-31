package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.screen.dukanDetails.DuaknDetailsArgs.DUKAN_ID
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ProductUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.Style

class DukanDetailsViewModel(
    private val dukanManagementRepository: DukanManagementRepository,
    private val shelfRepository: ShelfRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<DukanDetailsUiState, DukanDetailsEffects>(
    DukanDetailsUiState(),
    defaultDispatcher = defaultDispatcher
), DukanDetailsInteractionListener {

    val dukanId: String = requireNotNull(savedStateHandle[DUKAN_ID])

    init {
        loadDukanDetails()
    }

    private fun loadDukanDetails() {
        tryToExecute(
            onStart = ::onLoadDukanDetailsStart,
            block = { dukanManagementRepository.getDukanDetailsByDukanId(dukanId) },
            onSuccess = ::onLoadDukanDetailsSuccess,
            onError = ::onLoadDukanDetailsError
        )
    }

    private fun onLoadDukanDetailsStart() {
        updateState {
            copy(
                dukanDetailsState = DukanDetailsUiState.DukanDetailsState.LOADING
            )
        }
    }

    private fun onLoadDukanDetailsSuccess(dukanDetails: Dukan) {
        updateState {
            copy(
                dukanInfo = dukanDetails.toUiState(),
                isDukanInfoLoading = false,
            )
        }
        loadShelvesPaging()
    }

    private fun onLoadDukanDetailsError(throwable: Throwable) {
        updateState {
            copy(
                isDukanInfoLoading = false,
                dukanDetailsState = DukanDetailsUiState.DukanDetailsState.ERROR
            )
        }
    }

    private fun isWideImageStyle() =
        state.value.dukanInfo.style == Style.WIDE_IMAGE

    private fun loadShelvesPaging() {
        tryToCollect(
            block = ::getShelvesPagingFlow,
            onCollect = ::onShelvesLoaded
        )
    }

    private fun getShelvesPagingFlow(): Flow<PagingData<ShelfUiState>> {
        return createPagingSourceFlow(mapper = { it.toUiState() }) { pageNumber, pageSize ->
            shelfRepository.getShelvesByDukanId(
                dukanId = dukanId,
                pageNumber = pageNumber,
                pageSize = pageSize
            ).items
        }
    }

    private fun onShelvesLoaded(shelves: PagingData<ShelfUiState>) {
        if (isWideImageStyle()) {
            updateState {
                copy(
                    shelves = flowOf(shelves)
                )
            }
            loadProductsPaging()
        } else {
            loadProductsLimited(shelves)
        }
    }

    private fun loadProductsLimited(shelves: PagingData<ShelfUiState>) {
        tryToExecute(
            block = { updateProductsLimited(shelves) },
            onSuccess = ::onProductsLimitedLoaded
        )
    }

    private fun updateProductsLimited(
        shelves: PagingData<ShelfUiState>
    ): PagingData<ShelfUiState> {
        return shelves.map { shelf ->
            val products = getProductsLimitedByShelfId(shelf.id)
            shelf.copy(products = products)
        }.filter { it.products.isNotEmpty() }
    }

    private suspend fun getProductsLimitedByShelfId(shelfId: String): List<ProductUiState> {
        val maxProducts = 6
        val page = 0
        val product = productRepository.getProductsByShelfId(shelfId, page, maxProducts).items
        return product.map { it.toUiState() }
    }

    private fun onProductsLimitedLoaded(updatedShelves: PagingData<ShelfUiState>) {
        updateState {
            copy(
                shelves = flowOf(updatedShelves),
                dukanDetailsState = DukanDetailsUiState.DukanDetailsState.LOADED
            )
        }
    }

    private fun loadProductsPaging() {
        tryToCollect(
            block = ::getProductPagingFlow,
            onCollect = ::onProductsLoaded
        )
    }

    private fun getProductPagingFlow(): Flow<PagingData<ProductUiState>> {
        val shelfId = state.value.shelfIdSelected
        if (shelfId.isNullOrEmpty()) {
            return flowOf(PagingData.empty())
        }

        return createPagingSourceFlow(mapper = { it.toUiState() }) { pageNumber, pageSize ->
            productRepository.getProductsByShelfId(
                shelfId = shelfId,
                page = pageNumber,
                size = pageSize
            ).items
        }
    }

    private fun onProductsLoaded(products: PagingData<ProductUiState>) {
        updateState {
            copy(
                productsShelf = flowOf(products),
                dukanDetailsState = DukanDetailsUiState.DukanDetailsState.LOADED
            )
        }
    }

    override fun onBackClicked() {
        emitEffect(DukanDetailsEffects.NavigateBack)
    }

    override fun onShelfClicked(id: String) {
        updateState { copy(shelfIdSelected = id) }

        if (isWideImageStyle()) {
            loadProductsPaging()
        }
    }

    override fun onViewAllProductsShelfClicked(id: String, name: String) {
        emitEffect(
            DukanDetailsEffects.NavigateToViewAllShelfProducts(
                id = id,
                name = name,
                style = state.value.dukanInfo.style.name,
                color = state.value.dukanInfo.color
            )
        )
    }

    override fun onViewDukanOnMapClicked(latitude: Double, longitude: Double) {
        emitEffect(DukanDetailsEffects.NavigateToViewDukanOnMap(latitude, longitude))
    }

    override fun onAddToCartClicked(productId: String) {
        tryToExecute(
            block = {
                state.value.shelves.collectLatest {
                    updateShelvesWithAddedProduct(
                        it,
                        productId
                    )
                }
            }
        )
    }

    override fun onRetryClicked() {
        loadDukanDetails()
    }

    override fun onProductClicked(productId: String) {
        emitEffect(DukanDetailsEffects.NavigateToProductDetails(productId))
    }

    private fun updateShelvesWithAddedProduct(
        shelves: PagingData<ShelfUiState>,
        productId: String
    ): PagingData<ShelfUiState> {
        return shelves.map { shelf ->
            shelf.copy(products = updateProductsWithAddedItem(shelf.products, productId))
        }
    }

    private fun updateProductsWithAddedItem(
        products: List<ProductUiState>,
        productId: String
    ): List<ProductUiState> {
        return products.map { product ->
            if (product.id == productId) product.copy(inCartQuantity = 1) else product
        }
    }
}

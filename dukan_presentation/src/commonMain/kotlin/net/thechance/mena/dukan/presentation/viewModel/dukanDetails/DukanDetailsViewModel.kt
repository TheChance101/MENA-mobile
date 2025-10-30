package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
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

    private val productsState: MutableStateFlow<PagingData<ProductUiState>> =
        MutableStateFlow(PagingData.empty())

    private val shelvesState: MutableStateFlow<PagingData<ShelfUiState>> =
        MutableStateFlow(PagingData.empty())

    val shelfProductsState: MutableStateFlow<List<ProductUiState>> = MutableStateFlow(emptyList())


    private val args = savedStateHandle.toRoute<DukanRoute.DukanDetails>()

    init {
        loadDukanDetails()
    }

    private fun loadDukanDetails() {
        tryToExecute(
            onStart = ::onLoadDukanDetailsStart,
            block = { dukanManagementRepository.getDukanDetailsByDukanId(args.dukanId) },
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

    private fun loadShelvesPaging() {
        tryToCollect(
            block = ::getShelvesPagingFlow,
            onCollect = ::onShelvesLoaded
        )
    }

    private fun getShelvesPagingFlow(): Flow<PagingData<ShelfUiState>> {
        return createPagingSourceFlow(mapper = { it.toUiState() }) { pageNumber, pageSize ->
            shelfRepository.getShelvesByDukanId(
                dukanId = args.dukanId,
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
        shelvesState.value = updatedShelves
        updateState {
            copy(
                shelves = shelvesState,
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
        productsState.value = products
        updateState {
            copy(
                productsShelf = productsState,
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
                color = state.value.dukanInfo.color,
                dukanId = args.dukanId
            )
        )
    }

    override fun onViewDukanOnMapClicked(latitude: Double, longitude: Double) {
        emitEffect(DukanDetailsEffects.NavigateToViewDukanOnMap(latitude, longitude))
    }

    private fun updateProductListInShelfPagingData(
        productId: String,
        updateProduct: (ProductUiState) -> ProductUiState
    ) {
        val currentData = shelvesState.value
        val updatedData = currentData.map { shelf ->
            shelf.copy(
                products = shelf.products.map { product ->
                    if (product.id == productId) updateProduct(product) else product
                }
            )
        }

        shelvesState.value = updatedData
        updateState { copy(shelves = shelvesState) }
    }

    private fun updateProductInPagingData(
        productId: String,
        updateProduct: (ProductUiState) -> ProductUiState
    ) {
        val currentData = productsState.value
        val updatedData = currentData.map { product ->
            if (product.id == productId) updateProduct(product) else product
        }

        productsState.value = updatedData
        updateState { copy(productsShelf = productsState) }
    }


    override fun onAddToCartClicked(productId: String) {
        if (isWideImageStyle()) {
            updateProductInPagingData(productId) { product ->
                product.copy(showProductQuantity = true)
            }
        } else {
            updateProductListInShelfPagingData(productId) { product ->
                product.copy(showProductQuantity = true)
            }
        }

    }

    private fun increaseProductQuantity(productId: String) {
        updateProductQuantity(productId) { product ->
            product.copy(inCartQuantity = product.inCartQuantity + 1)
        }

    }

    private fun decreaseProductQuantity(productId: String) {
        updateProductQuantity(productId) { product ->
            if (product.inCartQuantity == 1) {
                product.copy(showProductQuantity = false)
            } else product.copy(inCartQuantity = product.inCartQuantity - 1)
        }
    }

    private fun updateProductQuantity(
        productId: String,
        update: (ProductUiState) -> ProductUiState
    ) {
        if (isWideImageStyle()) {
            updateProductInPagingData(productId, update)
        } else {
            updateProductListInShelfPagingData(productId, update)
        }
    }

    override fun onPlusClicked(productId: String) {
        increaseProductQuantity(productId)
        tryToExecuteWithDebounce(
            block = {
                // update product
            },
        )
    }

    override fun onMinusClicked(productId: String) {
        decreaseProductQuantity(productId)
        tryToExecuteWithDebounce(
            block = {
                // update product
            },
        )
    }

    override fun onCartClicked() {
        emitEffect(DukanDetailsEffects.NavigateToCartScreen(args.dukanId))
    }

    override fun onRetryClicked() {
        loadDukanDetails()
    }

    private fun isWideImageStyle() =
        state.value.dukanInfo.style == Style.WIDE_IMAGE

}

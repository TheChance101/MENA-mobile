package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
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
            block = { dukanManagementRepository.getDukanDetailsByDukanId(dukanId) },
            onSuccess = ::onLoadDukanDetailsSuccess,
            onError = ::onLoadDukanDetailsError
        )
    }

    private fun onLoadDukanDetailsSuccess(dukanDetails: Dukan) {
        updateState {
            copy(
                dukanInfo = dukanDetails.toUiState(),
                isDukanInfoLoading = false
            )
        }
        loadShelvesFromRepository()
    }

    private fun onLoadDukanDetailsError(throwable: Throwable) {
        updateState { copy(isDukanInfoLoading = false) }
    }

    private fun loadShelvesFromRepository() {
        tryToCollect(
            block = {
                createPagingSourceFlow(mapper = { it.toUiState() }) { pageNumber, pageSize ->
                    shelfRepository.getShelvesByDukanId(
                        dukanId = dukanId,
                        pageNumber = pageNumber,
                        pageSize = pageSize
                    ).items
                }
            },
            onCollect = ::onShelvesLoaded
        )
    }

    private fun onShelvesLoaded(shelves: PagingData<DukanDetailsUiState.ShelfUiState>) {
        if (isWideImageStyle()) {
            handleWideImageShelves(shelves)
        } else {
            handleNonWideImageShelves(shelves)
        }
        loadProductsFromRepository()
    }

    private fun handleNonWideImageShelves(
        shelves: PagingData<DukanDetailsUiState.ShelfUiState>,
    ) {
//        viewModelScope.launch {
//            val (updatedShelves, firstShelfId) = updateProductsShelves(shelves)
//            updateState {
//                copy(
//                    shelves = flowOf(PagingData.from(updatedShelves)),
//                    shelfIdSelected = state.value.shelfIdSelected ?: firstShelfId
//                )
//            }
//        }
    }

    private fun handleWideImageShelves(
        shelves: PagingData<DukanDetailsUiState.ShelfUiState>,
    ) {
//        updateState {
//            copy(
//                shelves = flowOf(shelves),
//                shelfIdSelected = shelves.items.firstOrNull()?.id
//            )
//        }
    }

    private fun isWideImageStyle() =
        state.value.dukanInfo.style == DukanDetailsUiState.Style.WIDE_IMAGE

//    private suspend fun updateProductsShelves(
//        shelves: PagingData<DukanDetailsUiState.ShelfUiState>
//    ): Pair<List<DukanDetailsUiState.ShelfUiState>, String?> = coroutineScope {
//        val updatedShelvesWithProducts = shelves.items
//            .map { shelf ->
//                async {
//                    val products = getInitialProductsForShelf(shelf.id)
//                    shelf.copy(products = products)
//                }
//            }
//            .awaitAll()
//            .filter { it.products.isNotEmpty() }
//
//        val firstShelfId = updatedShelvesWithProducts.firstOrNull()?.id
//        updatedShelvesWithProducts to firstShelfId
//    }

    private suspend fun getInitialProductsForShelf(shelfId: String): List<DukanDetailsUiState.ProductUiState> {
        val maxProducts = 6
        val page = 0
        val product = productRepository.getProductsByShelfId(shelfId, page, maxProducts).items
        return product.map { it.toUiState() }
    }

    private fun loadProductsFromRepository() {
        val shelfId = state.value.shelfIdSelected
        if (shelfId.isNullOrEmpty()) return

        tryToCollect(
            block = { createProductPagingFlow(shelfId) },
            onCollect = ::onProductsLoaded
        )
    }

    private fun createProductPagingFlow(shelfId: String): Flow<PagingData<DukanDetailsUiState.ProductUiState>> {
        return createPagingSourceFlow(mapper = { it.toUiState() }) { pageNumber, pageSize ->
            productRepository.getProductsByShelfId(
                shelfId = shelfId,
                page = pageNumber,
                size = pageSize
            ).items
        }
    }

    private fun onProductsLoaded(products: PagingData<DukanDetailsUiState.ProductUiState>) {
        updateState {
            copy(
                productsShelf = flowOf(products),
            )
        }
    }

    override fun onBackClicked() {
        emitEffect(DukanDetailsEffects.NavigateBack)
    }

    override fun onShelfClicked(id: String) {
        updateState {
            copy(
                shelfIdSelected = id,
            )
        }
        if (state.value.dukanInfo.style == DukanDetailsUiState.Style.WIDE_IMAGE) {
            loadProductsFromRepository()
        }
    }

    override fun onViewAllShelfProductsClicked(id: String, name: String) {
        emitEffect(
            DukanDetailsEffects.NavigateToViewAllShelfProducts(
                id = id,
                name = name,
                style = state.value.dukanInfo.style.name,
                color = state.value.dukanInfo.color
            )
        )
        updateState {
            copy(
                shelfIdSelected = id,
            )
        }
        loadProductsFromRepository()
    }

    override fun onViewDukanOnMapClicked(latitude: Double, longitude: Double) {
        emitEffect(DukanDetailsEffects.NavigateToViewDukanOnMap(latitude, longitude))
    }

    override fun onAddToCartClick(productId: String) {
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

    private fun updateShelvesWithAddedProduct(
        shelves: PagingData<DukanDetailsUiState.ShelfUiState>,
        productId: String
    ): PagingData<DukanDetailsUiState.ShelfUiState> {
        return shelves.map { shelf ->
            shelf.copy(products = updateProductsWithAddedItem(shelf.products, productId))
        }
    }

    private fun updateProductsWithAddedItem(
        products: List<DukanDetailsUiState.ProductUiState>,
        productId: String
    ): List<DukanDetailsUiState.ProductUiState> {
        return products.map { product ->
            if (product.id == productId) product.copy(inCartQuantity = 1) else product
        }
    }
}

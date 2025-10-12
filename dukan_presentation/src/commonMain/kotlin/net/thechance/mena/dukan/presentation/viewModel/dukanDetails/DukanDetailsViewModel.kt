package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.navigation.SavedStateHandleArgs.DUKAN_ID
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.pagination.base.createPagingSource
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class DukanDetailsViewModel(
    private val dukanRepository: DukanRepository,
    private val shelfRepository: ShelfRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<DukanDetailsUiState, DukanDetailsEffects>(
    DukanDetailsUiState(),
    defaultDispatcher = defaultDispatcher
),
    DukanDetailsInteractionListener {
    val dukanId: String = requireNotNull(savedStateHandle[DUKAN_ID])

    init {
        loadDukanDetails()
    }

    private fun loadDukanDetails() {
        tryToExecute(
            block = {
                dukanRepository.getDukanDetailsByDukanId(dukanId)
            },
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
        updateState {
            copy(isDukanInfoLoading = false)
        }
    }

    private fun loadShelvesFromRepository() {
        tryToCollect(
            block = { pagerShelf.flow },
            onCollect = ::onShelvesLoaded
        )
        viewModelScope.launch {
            pagerShelf.load()
        }
    }

    private fun onShelvesLoaded(shelves: PagingData<DukanDetailsUiState.ShelfUiState>) {
        val shelfState = when {
            shelves.isLoading && shelves.items.isEmpty() -> DukanDetailsUiState.ShelvesState.LOADING
            shelves.items.isEmpty() -> DukanDetailsUiState.ShelvesState.EMPTY
            else -> DukanDetailsUiState.ShelvesState.LOADED
        }
        updateProductsShelves(shelves)
        updateState {
            copy(
                shelves = shelves,
                shelvesState = shelfState,
                shelfIdSelected = shelves.items.firstOrNull()?.id.orEmpty()
            )
        }
        loadProductsForSelectedShelf()
    }

    private fun updateProductsShelves(shelves: PagingData<DukanDetailsUiState.ShelfUiState>) {
        if (state.value.dukanInfo.style != DukanDetailsUiState.Style.WIDE_IMAGE) {
            viewModelScope.launch {
                shelves.copy(
                    items = shelves.items.map {
                        it.copy(
                            products = getInitialProductsForShelf(it.id)
                        )
                    }
                )
            }
        }
    }

    private suspend fun getInitialProductsForShelf(shelfId: String): List<DukanDetailsUiState.ProductUiState> {
        val maxProducts = 6
        val page = 1
        val product = productRepository.getProductsByShelfId(shelfId, page, maxProducts).items
        return product.map { it.toUiState() }
    }

    private fun loadProductsForSelectedShelf() {
        viewModelScope.launch {
            pagerProduct.refresh()
        }
        loadProductsFromRepository()
        viewModelScope.launch {
            pagerProduct.refresh()
        }
    }

    private fun loadProductsFromRepository() {
        tryToCollect(
            onStart = ::onProductsStart,
            block = { pagerProduct.flow },
            onCollect = ::onProductsLoaded
        )
        viewModelScope.launch {
            pagerProduct.load()
        }
    }

    private fun onProductsStart() {
        updateState {
            copy(
                productsState = DukanDetailsUiState.ProductsState.LOADING,
                productsShelf = PagingData()
            )
        }
    }

    private fun onProductsLoaded(products: PagingData<DukanDetailsUiState.ProductUiState>) {
        val productsState = when {
            products.isLoading && products.items.isEmpty() -> DukanDetailsUiState.ProductsState.LOADING
            products.items.isEmpty() -> DukanDetailsUiState.ProductsState.EMPTY
            else -> DukanDetailsUiState.ProductsState.LOADED
        }
        updateState {
            copy(
                productsShelf = products,
                productsState = productsState,
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
                productsShelf = PagingData()
            )
        }
        if (state.value.dukanInfo.style == DukanDetailsUiState.Style.WIDE_IMAGE) {
            loadProductsForSelectedShelf()
        }
    }


    override fun onViewAllShelfProductsClicked(id: String, name: String) {
        emitEffect(DukanDetailsEffects.NavigateToViewAllShelfProducts(id, name))
        updateState {
            copy(
                shelfIdSelected = id,
                productsShelf = PagingData()
            )
        }
        loadProductsForSelectedShelf()
    }

    override fun onViewDukanOnMapClicked(latitude: Double, longitude: Double) {
        emitEffect(DukanDetailsEffects.NavigateToViewDukanOnMap(latitude, longitude))
    }


    val pagerShelf = createPagingSource(
        mapper = { it.toUiState() }
    ) {
        shelfRepository.getShelvesByDukanId(
            dukanId = dukanId,
            pageNumber = it,
            pageSize = 20
        )
    }
    val pagerProduct = createPagingSource(
        mapper = { it.toUiState() }
    ) {
        productRepository.getProductsByShelfId(
            shelfId = state.value.shelfIdSelected.toString(),
            page = it,
            size = 20
        )
    }
}
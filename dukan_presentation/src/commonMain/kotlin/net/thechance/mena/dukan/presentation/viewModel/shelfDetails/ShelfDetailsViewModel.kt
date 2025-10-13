package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.presentation.navigation.SavedStateHandleArgs.SHELF_ID
import net.thechance.mena.dukan.presentation.navigation.SavedStateHandleArgs.SHELF_NAME
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.pagination.base.createPagingSource
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class ShelfDetailsViewModel(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ShelfDetailsUiState, ShelfDetailsEffects>(
    ShelfDetailsUiState(),
    defaultDispatcher = defaultDispatcher
), ShelfDetailsInteractionListener {
    val shelfId: String = requireNotNull(savedStateHandle[SHELF_ID])
    val shelfNamee: String = requireNotNull(savedStateHandle[SHELF_NAME])

    val pagerProduct = createPagingSource(
        mapper = { it.toUiState() }
    ) {
        productRepository.getProductsByShelfId(
            shelfId = shelfId,
            page = it,
            size = 20
        )
    }

    init {
        updateState { copy(shelfName = shelfNamee) }
        loadProductsFromRepository()
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
                productsState = ShelfDetailsUiState.ProductsState.LOADING,
                productsShelf = PagingData()
            )
        }
    }

    private fun onProductsLoaded(products: PagingData<ShelfDetailsUiState.ProductUiState>) {
        val productsState = when {
            products.isLoading && products.items.isEmpty() -> ShelfDetailsUiState.ProductsState.LOADING
            products.items.isEmpty() -> ShelfDetailsUiState.ProductsState.EMPTY
            else -> ShelfDetailsUiState.ProductsState.LOADED
        }
        updateState {
            copy(
                productsShelf = products,
                productsState = productsState,
            )
        }
    }


    override fun onBackClicked() {
        emitEffect(ShelfDetailsEffects.NavigateBack)
    }

    override fun onCartClick(productId: String) {
        updateState {
            copy(
                productsShelf = productsShelf.copy(
                    items = productsShelf.items.map { product ->
                        if (product.id == productId) {
                            product.copy(showProductQuantity = true)
                        } else {
                            product
                        }
                    }
                )
            )
        }
    }
}
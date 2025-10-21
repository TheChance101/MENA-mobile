package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.presentation.screen.shelfDetails.ShelfDetailsArgs.DUKAN_COLOR
import net.thechance.mena.dukan.presentation.screen.shelfDetails.ShelfDetailsArgs.DUKAN_STYLE
import net.thechance.mena.dukan.presentation.screen.shelfDetails.ShelfDetailsArgs.SHELF_ID
import net.thechance.mena.dukan.presentation.screen.shelfDetails.ShelfDetailsArgs.SHELF_NAME
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
    val shelfName: String = requireNotNull(savedStateHandle[SHELF_NAME])
    val dukanStyle: String = requireNotNull(savedStateHandle[DUKAN_STYLE])
    val dukancolor: Long = requireNotNull(savedStateHandle[DUKAN_COLOR])

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
        updateState {
            copy(
                shelfName = this@ShelfDetailsViewModel.shelfName,
                dukanStyle = ShelfDetailsUiState.Style.valueOf(this@ShelfDetailsViewModel.dukanStyle),
                dukancolor = this@ShelfDetailsViewModel.dukancolor
            )
        }
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

    private fun onProductsLoaded(products: PagingData<ShelfDetailsUiState.ProductUiState>) =
        updateState {
            val state = when {
                products.isLoading && products.items.isEmpty() -> ShelfDetailsUiState.ProductsState.LOADING
                products.error != null -> ShelfDetailsUiState.ProductsState.ERROR
                else -> ShelfDetailsUiState.ProductsState.LOADED
            }
            copy(productsShelf = products, productsState = state)
        }

    override fun onBackClicked() {
        emitEffect(ShelfDetailsEffects.NavigateBack)
    }

    override fun onAddToCartClick(productId: String) {
        updateState {
            val updatedItems = productsShelf.items.map {
                if (it.id == productId) it.copy(inCartQuantity = 1) else it
            }
            copy(productsShelf = productsShelf.copy(items = updatedItems))
        }
    }
}
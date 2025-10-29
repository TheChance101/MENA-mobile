package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.presentation.screen.shelfDetails.ShelfDetailsArgs.DUKAN_COLOR
import net.thechance.mena.dukan.presentation.screen.shelfDetails.ShelfDetailsArgs.DUKAN_STYLE
import net.thechance.mena.dukan.presentation.screen.shelfDetails.ShelfDetailsArgs.SHELF_ID
import net.thechance.mena.dukan.presentation.screen.shelfDetails.ShelfDetailsArgs.SHELF_NAME
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

    private val productsState: MutableStateFlow<PagingData<ShelfDetailsUiState.ProductUiState>> =
        MutableStateFlow(PagingData.empty())

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
            block = ::createPagingSource,
            onCollect = ::onProductsLoaded
        )
    }

    private fun createPagingSource(): Flow<PagingData<ShelfDetailsUiState.ProductUiState>> {
        return createPagingSourceFlow(
            mapper = { it.toUiState() }
        ) { pageNumber, pageSize ->
            productRepository.getProductsByShelfId(
                shelfId = shelfId,
                page = pageNumber,
                size = pageSize
            ).items
        }
    }

    private fun onProductsLoaded(products: PagingData<ShelfDetailsUiState.ProductUiState>) {
        productsState.value = products
        updateState {
            copy(productsShelf = productsState)
        }
    }

    private fun updateProductInPagingData(
        productId: String,
        updateProduct: (ShelfDetailsUiState.ProductUiState) -> ShelfDetailsUiState.ProductUiState
    ) {
        val currentData = productsState.value
        val updatedData = currentData.map { product ->
            if (product.id == productId) updateProduct(product) else product
        }

        productsState.value = updatedData
        updateState { copy(productsShelf = productsState) }
    }

    override fun onBackClicked() {
        emitEffect(ShelfDetailsEffects.NavigateBack)
    }

    override fun onAddToCartClicked(productId: String) {
        updateProductInPagingData(productId) { product ->
            product.copy(showProductQuantity = true)
        }
    }

    override fun onPlusClicked(productId: String) {

    }

    override fun onMinusClicked(productId: String) {
    }

    override fun onCartClicked() {
//        emitEffect(ShelfDetailsEffects.NavigateToCart(dukanId))
    }
}
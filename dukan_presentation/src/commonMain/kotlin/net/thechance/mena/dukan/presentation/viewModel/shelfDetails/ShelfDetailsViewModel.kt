package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import net.thechance.mena.dukan.domain.repository.DukanCartRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class ShelfDetailsViewModel(
    private val productRepository: ProductRepository,
    private val dukanCartRepository: DukanCartRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ShelfDetailsUiState, ShelfDetailsEffects>(
    ShelfDetailsUiState(),
    defaultDispatcher = defaultDispatcher
), ShelfDetailsInteractionListener {
    private val args = savedStateHandle.toRoute<DukanRoute.ShelfDetails>()

    private val productsState: MutableStateFlow<PagingData<ShelfDetailsUiState.ProductUiState>> =
        MutableStateFlow(PagingData.empty())

    init {
        updateState {
            copy(
                shelfName = args.shelfName,
                dukanStyle = ShelfDetailsUiState.Style.valueOf(args.dukanStyle),
                dukancolor = args.dukancolor
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
                shelfId = args.shelfId,
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
        product: ShelfDetailsUiState.ProductUiState,
        updateProduct: (ShelfDetailsUiState.ProductUiState) -> ShelfDetailsUiState.ProductUiState
    ) {
        val currentData = productsState.value
        val updatedData = currentData.map { productState ->
            if (productState.id == product.id) updateProduct(productState) else productState
        }

        productsState.value = updatedData
        updateState { copy(productsShelf = productsState) }
    }

    override fun onBackClicked() {
        emitEffect(ShelfDetailsEffects.NavigateBack)
    }

    override fun onAddToCartClicked(product: ShelfDetailsUiState.ProductUiState) {
        updateProductInPagingData(product) { product ->
            product.copy(showProductQuantity = true)
        }
    }

    private fun increaseProductQuantity(product: ShelfDetailsUiState.ProductUiState) {
        updateProductInPagingData(product) { product ->
            product.copy(inCartQuantity = product.inCartQuantity + 1)
        }
    }

    private fun decreaseProductQuantity(product: ShelfDetailsUiState.ProductUiState) {
        updateProductInPagingData(product) { product ->
            if (product.inCartQuantity == 1) {
                product.copy(showProductQuantity = false)
            } else product.copy(inCartQuantity = product.inCartQuantity - 1)
        }
    }

    override fun onPlusClicked(product: ShelfDetailsUiState.ProductUiState) {
        increaseProductQuantity(product)
        onQuantityChangedRequest(product)
    }

    override fun onMinusClicked(product: ShelfDetailsUiState.ProductUiState) {
        decreaseProductQuantity(product)
        onQuantityChangedRequest(product)
    }

    private fun onQuantityChangedRequest(product: ShelfDetailsUiState.ProductUiState) {

        val params = product.toUpdateProductCartQuantityParams(args.dukanId)

        tryToExecuteWithDebounce (
            debounceTime = 300,
            block = { dukanCartRepository.updateProductQuantity(params) }
        )

    }

    override fun onCartClicked() {
        emitEffect(ShelfDetailsEffects.NavigateToCart(args.dukanId))
    }
}
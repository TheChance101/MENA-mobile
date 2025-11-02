package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
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
        updateState {
            copy(productsShelf = flowOf(products))
        }
    }

    override fun onBackClicked() {
        emitEffect(ShelfDetailsEffects.NavigateBack)
    }

    override fun onAddToCartClicked(productId: String) {

        val params = UpdateProductCartQuantityParams(
            productId = productId,
            quantity = 1,
            dukanId = args.dukanId
        )

        tryToExecuteWithDebounce(
            debounceTime = 300,
            block = { dukanCartRepository.updateProductQuantity(params) }
        )
    }

    override fun onPlusClicked(productId: String, productQuantity: Int) {

        val params = UpdateProductCartQuantityParams(
            productId = productId,
            quantity = productQuantity,
            dukanId = args.dukanId
        )

        tryToExecuteWithDebounce(
            debounceTime = 300,
            block = { dukanCartRepository.updateProductQuantity(params) }
        )
    }

    override fun onMinusClicked(productId: String, productQuantity: Int) {

        val params = UpdateProductCartQuantityParams(
            productId = productId,
            quantity = productQuantity,
            dukanId = args.dukanId
        )

        tryToExecuteWithDebounce(
            debounceTime = 300,
            block = { dukanCartRepository.updateProductQuantity(params) }
        )
    }

    override fun onCartClicked() {
        emitEffect(ShelfDetailsEffects.NavigateToCart(args.dukanId))
    }
}
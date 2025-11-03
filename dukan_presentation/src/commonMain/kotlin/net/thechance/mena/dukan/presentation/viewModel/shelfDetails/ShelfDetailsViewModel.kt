package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.no_internet_connection
import mena.dukan_presentation.generated.resources.something_went_wrong
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ProductUiState
import org.jetbrains.compose.resources.StringResource

class ShelfDetailsViewModel(
    private val productRepository: ProductRepository,
    private val dukanCartRepository: CartRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ShelfDetailsUiState, ShelfDetailsEffects>(
    ShelfDetailsUiState(),
    defaultDispatcher = defaultDispatcher
), ShelfDetailsInteractionListener {
    private val args = savedStateHandle.toRoute<DukanRoute.ShelfDetails>()

    init {
        println(args.dukanStyle)
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

    override fun onAddToCartClicked(
        productId: String,
        productQuantity: Int,
    ) {

        val uiRequest = ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)

        tryToExecuteWithDebounce(
            block = {
                if (productQuantity == 1) dukanCartRepository.addProductQuantity(domainRequest)
                else dukanCartRepository.updateProductQuantity(domainRequest)
            },
            onError = ::onErrorUpdateProductQuantity
        )
    }

    override fun onPlusClicked(
        productId: String,
        productQuantity: Int,
    ) {

        val uiRequest = ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)

        tryToExecuteWithDebounce(
            block = { dukanCartRepository.updateProductQuantity(domainRequest) },
            onError = {
                onErrorUpdateProductQuantity(it)
            }
        )
    }

    override fun onMinusClicked(
        productId: String,
        productQuantity: Int,
    ) {

        val uiRequest = ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)

        tryToExecuteWithDebounce(
            block = {
                if (productQuantity == 1) deleteProductFromCart(productId)
                else dukanCartRepository.updateProductQuantity(domainRequest)
            },
            onError = {
                onErrorUpdateProductQuantity(it)
            }
        )
    }

    private fun deleteProductFromCart(productId: String) {
        tryToExecuteWithDebounce(
            block = {
                dukanCartRepository.deleteProductFromCart(
                    dukanId = args.dukanId,
                    productId = productId
                )
            },
            onError = ::onErrorUpdateProductQuantity
        )
    }

    private fun onErrorUpdateProductQuantity(throwable: Throwable) {
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            else -> Res.string.something_went_wrong
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
    }

    private fun showSnackBar(message: StringResource, type: SnackBarType) {
        updateState {
            copy(
                snackBarState = SnackBarUiState(
                    message = message,
                    snackBarType = type
                )
            )
        }
    }

    override fun onDismissSnackBar() {
        updateState {
            copy(
                snackBarState = null
            )
        }
    }

    override fun onViewCartClicked() {
        emitEffect(ShelfDetailsEffects.NavigateToCart(args.dukanId))
    }

    override fun onProductClicked(productId: String) {
        emitEffect(ShelfDetailsEffects.NavigateToProductDetails(productId, args.dukanId))
    }
}
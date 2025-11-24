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
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.no_internet_connection
import mena.dukan_presentation.generated.resources.something_went_wrong
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource

class ShelfDetailsViewModel(
    private val productRepository: ProductRepository,
    private val dukanCartRepository: CartRepository,
    private val dukanManagementRepository: DukanManagementRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ShelfDetailsUiState, ShelfDetailsEffects>(
    ShelfDetailsUiState(),
    defaultDispatcher = defaultDispatcher
), ShelfDetailsInteractionListener {
    private val args = savedStateHandle.toRoute<DukanRoute.ShelfDetails>()

    private val shelfProductsMutableStateFlow =
        MutableStateFlow<PagingData<ShelfDetailsUiState.ProductUiState>>(PagingData.empty())

    init {
        updateState { copy(shelfName = args.shelfName) }
        loadDukanDetails()
        loadProductsFromRepository()
        loadCartInfo()
    }

    private fun loadCartInfo() {
        tryToExecute(
            block = { dukanCartRepository.getCartInfo(args.dukanId) },
            onError = ::onCartInfoError,
            onSuccess = ::onLoadCartSuccess
        )
    }

    private fun onCartInfoError(throwable: Throwable) {
        updateState { copy(hasProductInCart = false) }
    }

    private fun onLoadCartSuccess(cart: Cart) {
        updateState { copy(hasProductInCart = cart.totalPriceAfterDiscount > 0) }
    }

    private fun loadDukanDetails() {
        tryToExecute(
            block = { dukanManagementRepository.getDukanDetailsByDukanId(args.dukanId) },
            onSuccess = ::onLoadDukanDetailsSuccess,
        )
    }

    private fun onLoadDukanDetailsSuccess(dukanDetails: Dukan) {
        updateState {
            copy(
                dukanStyle = dukanDetails.style.toShelfStyle(),
                dukancolor = dukanDetails.color.toUiColor().color
            )
        }
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
        shelfProductsMutableStateFlow.value = products
        shelfProductsMutableStateFlow.value = shelfProductsMutableStateFlow.value.map {
            updateProductQuantityInCart(productId = it.id, newQuantity = it.inCartQuantity)
            it
        }
        updateState {
            copy(productsShelf = shelfProductsMutableStateFlow)
        }
    }

    override fun onBackClicked() {
        emitEffect(ShelfDetailsEffects.NavigateBack)
    }

    override fun onAddToCartClicked(
        productId: String,
        productQuantity: Int,
    ) {
        updateProductQuantityInCart(productId, productQuantity)
        updateState { copy(hasProductInCart = true) }

        val uiRequest =
            ShelfDetailsUiState.ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)

        tryToExecute(
            block = { dukanCartRepository.addProductQuantity(domainRequest) },
            onError = ::onErrorUpdateProductQuantity
        )
    }

    override fun onPlusClicked(
        productId: String,
        productQuantity: Int,
    ) {
        updateProductQuantityInCart(productId, productQuantity)
        updateState { copy(hasProductInCart = true) }

        val uiRequest =
            ShelfDetailsUiState.ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)

        tryToExecuteWithDebounce(
            block = { dukanCartRepository.updateProductQuantity(domainRequest) },
            onError = ::onErrorUpdateProductQuantity
        )
    }

    override fun onMinusClicked(
        productId: String,
        productQuantity: Int,
    ) {
        updateProductQuantityInCart(productId, productQuantity)

        val uiRequest =
            ShelfDetailsUiState.ProductUiState(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(args.dukanId)

        tryToExecuteWithDebounce(
            block = {
                onMinusClickedBlock(
                    productId = productId,
                    productQuantity = productQuantity,
                    domainRequest = domainRequest
                )
            },
            onError = ::onErrorUpdateProductQuantity
        )
    }

    private suspend fun onMinusClickedBlock(
        productQuantity: Int,
        productId: String,
        domainRequest: UpdateProductCartQuantityParams
    ) {
        if (productQuantity == 0) deleteProductFromCart(productId)
        else dukanCartRepository.updateProductQuantity(domainRequest)
    }

    private fun deleteProductFromCart(productId: String) {
        tryToExecute(
            block = {
                dukanCartRepository.deleteProductFromCart(
                    dukanId = args.dukanId,
                    productId = productId
                )
            },
            onSuccess = {
                loadCartInfo()
            }
        )
    }

    private fun onErrorUpdateProductQuantity(throwable: Throwable) {
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            else -> Res.string.something_went_wrong
        }
        showSnackBar(message = messageRes)
    }

    private fun showSnackBar(message: StringResource, type: SnackBarType = SnackBarType.ERROR) {
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

    private fun updateProductQuantityInCart(productId: String, newQuantity: Int) {
        updateState {
            copy(
                productQuantity = productQuantity + (productId to newQuantity)
            )
        }
    }

    override fun onViewCartClicked() {
        emitEffect(ShelfDetailsEffects.NavigateToCart(args.dukanId))
    }

    override fun onProductClicked(productId: String) {
        emitEffect(ShelfDetailsEffects.NavigateToProductDetails(productId, args.dukanId))
    }

    fun refreshProducts() {
        loadProductsFromRepository()
        loadCartInfo()
    }

}
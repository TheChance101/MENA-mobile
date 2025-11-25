package net.thechance.mena.dukan.presentation.viewModel.dukanCart

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
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.no_internet_connection
import mena.dukan_presentation.generated.resources.something_went_wrong
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.exceptions.NoSuchItemException
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState.CartState
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState.DukanInfoState
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState.ProductUiState
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class DukanCartViewModel(
    private val cartRepository: CartRepository,
    private val dukanRepository: DukanManagementRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<DukanCartUiState, DukanCartEffects>(
    initialState = DukanCartUiState(),
    defaultDispatcher = defaultDispatcher
),
    DukanCartInteractionListener {
    private val dukanId = savedStateHandle.toRoute<DukanRoute.DukanCart>().dukanId
    private var productsMutableStateFlow =
        MutableStateFlow<PagingData<ProductUiState>>(PagingData.empty())

    init {
        loadCart()
    }

    private fun loadCart() {
        loadCartInfo()
        loadDukanInfo()
        loadProductsPaging()
    }

    private fun loadCartInfo() {
        tryToExecute(
            onStart = { updateState { copy(cartState = CartState.LOADING) } },
            block = { cartRepository.getCartInfo(dukanId) },
            onError = ::onCartInfoError,
            onSuccess = ::onLoadCartSuccess
        )
    }

    private fun updateTotalPrice() {
        tryToExecute(
            block = { cartRepository.getCartInfo(dukanId) },
            onError = ::onCartInfoError,
            onSuccess = ::onLoadCartSuccess
        )
    }

    private fun onCartInfoError(throwable: Throwable) {
        when (throwable) {
            is NoSuchItemException -> updateState {
                copy(totalPrice = 0.0, cartState = CartState.LOADED)
            }

            is NoInternetException -> updateState { copy(cartState = CartState.NO_INTERNET) }
            else -> showSnackBar(message = Res.string.something_went_wrong)
        }
    }

    private fun onLoadCartSuccess(cart: Cart) {
        updateState {
            copy(
                totalPrice = cart.totalPriceAfterDiscount,
                cartState = CartState.LOADED
            )
        }
    }


    private fun loadDukanInfo() {
        tryToExecute(
            onStart = { updateState { copy(dukanInfoState = DukanInfoState.LOADING) } },
            block = { dukanRepository.getDukanDetailsByDukanId(dukanId) },
            onSuccess = ::onDukanInfoSuccess
        )
    }

    private fun onDukanInfoSuccess(dukan: Dukan) {
        updateState {
            copy(
                dukanInfo = dukan.toUiState(),
                dukanInfoState = DukanInfoState.LOADED
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

    private fun loadProductsPaging() {
        tryToCollect(
            block = ::getProductPagingFlow,
            onCollect = ::onProductsSuccess
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun getProductPagingFlow(): Flow<PagingData<ProductUiState>> {
        return createPagingSourceFlow(mapper = { it.toUiState() }) { pageNumber, pageSize ->
            cartRepository.getCartProducts(
                dukanId = Uuid.parse(dukanId),
                page = pageNumber,
                size = pageSize
            ).items
        }
    }

    private fun onProductsSuccess(products: PagingData<ProductUiState>) {
        productsMutableStateFlow.value = products
        productsMutableStateFlow.value.map {
            updateProductQuantityInCart(it.id, it.quantity)
            it
        }
        updateState { copy(products = productsMutableStateFlow) }
    }


    override fun onBackClicked() {
        emitEffect(DukanCartEffects.NavigateBack)
    }

    override fun onDukanClicked() {
        emitEffect(DukanCartEffects.NavigateToDukanDetails(dukanId))
    }

    override fun onCheckoutClicked() {
        emitEffect(DukanCartEffects.NavigateToCheckout(dukanId))
    }

    override fun onIncreaseItemQuantityClicked(productId: String, newQuantity: Int) {
        updateProductQuantityInCart(productId, newQuantity)
        updateProductQuantityInServer(productId, newQuantity)
    }

    override fun onDecreaseItemQuantityClicked(productId: String, newQuantity: Int) {
        if (newQuantity == 0) return
        updateProductQuantityInCart(productId, newQuantity)
        updateProductQuantityInServer(productId, newQuantity)
    }


    private fun updateProductQuantityInServer(productId: String, newQuantity: Int) {
        tryToExecuteWithDebounce(
            block = { uploadNewQuantityInServer(productId, newQuantity) },
            onError = ::onErrorUpdateProductQuantity,
            onSuccess = { updateTotalPrice() }
        )
    }

    private suspend fun uploadNewQuantityInServer(productId: String, newQuantity: Int) {
        cartRepository.updateProductQuantity(
            UpdateProductCartQuantityParams(
                dukanId = dukanId,
                productId = productId,
                quantity = newQuantity
            )
        )
    }


    override fun onRemoveItemClicked(productId: String) {
        removeProductInState(productId)
        removeProductInServer(productId)
    }

    private fun removeProductInState(productId: String) {
        updateProductQuantityInCart(productId, 0)
        val updateProducts = productsMutableStateFlow.value.filter {
            it.id != productId
        }
        productsMutableStateFlow.value = updateProducts
        updateState { copy(products = productsMutableStateFlow) }
    }

    private fun removeProductInServer(productId: String) {
        tryToExecute(
            block = {
                cartRepository.deleteProductFromCart(
                    dukanId = dukanId,
                    productId = productId
                )
            },
            onError = ::onErrorUpdateProductQuantity,
            onSuccess = { updateTotalPrice() }
        )
    }

    override fun onRetryLoadCartClicked() {
        loadCart()
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
        updateState { copy(snackBarState = null) }
    }
}
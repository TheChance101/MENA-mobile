package net.thechance.mena.dukan.presentation.viewModel.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.no_active_location
import mena.dukan_presentation.generated.resources.no_internet_connection
import mena.dukan_presentation.generated.resources.something_went_wrong
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.exceptions.NoSuchItemException
import net.thechance.mena.dukan.domain.model.Transaction
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.exception.InvalidCredentialsException
import net.thechance.mena.identity.domain.service.LocationService
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val locationService: LocationService,
    private val savedStateHandle: SavedStateHandle,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<CheckoutUiState, CheckoutEffect>(
    initialState = CheckoutUiState(),
    defaultDispatcher = dispatcher
), CheckoutInteractionListener {


    init {
        loadCartProductsFromRepository()
        loadDeliveryAddress()
        updateTotalPrice()
    }

    private fun loadCartProductsFromRepository() {
        tryToCollect(
            block = ::createPagingSource,
            onCollect = ::onProductsLoaded
        )
    }

    private fun createPagingSource(): Flow<PagingData<CheckoutUiState.CartItem>> {
        val args = savedStateHandle.toRoute<DukanRoute.CheckoutScreenRoute>()
        return createPagingSourceFlow(
            mapper = { it.toUiState() }
        ) { pageNumber, pageSize ->
            cartRepository.getCartProducts(
                dukanId = Uuid.parse(args.dukanId),
                page = pageNumber,
                size = pageSize
            ).items
        }
    }

    private fun onProductsLoaded(products: PagingData<CheckoutUiState.CartItem>) =
        updateState {
            copy(items = flowOf(products))
        }

    fun loadDeliveryAddress() {
        tryToExecute(
            block = ::getActiveAddress,
            onSuccess = ::getActiveAddressSuccess,
            onError = ::getActiveAddressError
        )
    }

    private suspend fun getActiveAddress(): Address? {
        return locationService.getActiveAddress()
    }

    private fun getActiveAddressSuccess(activeAddress: Address?) {
        updateState {
            copy(
                deliveryAddress = activeAddress.toUiState(),
                isConfirmOrderButtonEnabled = true
            )
        }
    }

    private fun getActiveAddressError(throwable: Throwable) {
        updateState { copy(isConfirmOrderButtonEnabled = false) }
        when (throwable) {
            is NoInternetException -> showSnackBar(message = Res.string.no_internet_connection)
            is InvalidCredentialsException -> showSnackBar(message = Res.string.no_active_location)
            else -> showSnackBar(message = Res.string.something_went_wrong)
        }
    }

    private fun updateTotalPrice() {
        tryToExecute(
            block = ::getTotalCartPrice,
            onSuccess = ::onLoadCartSuccess,
            onError = ::onCartInfoError,
        )
    }

    private suspend fun getTotalCartPrice(): Cart {
        val args = savedStateHandle.toRoute<DukanRoute.CheckoutScreenRoute>()
        updateState { copy(dukanId = args.dukanId) }
        return cartRepository.getCartInfo(args.dukanId)
    }

    private fun onLoadCartSuccess(cart: Cart) {
        updateState {
            copy(
                totalAmount = cart.totalPrice,
                cartId = cart.id,
                isConfirmOrderButtonEnabled = true
            )
        }
    }

    private fun onCartInfoError(throwable: Throwable) {
        updateState { copy(isConfirmOrderButtonEnabled = false) }
        when (throwable) {
            is NoSuchItemException -> updateState {
                copy(totalAmount = 0.0)
            }

            is NoInternetException -> showSnackBar(message = Res.string.no_internet_connection)
            else -> showSnackBar(message = Res.string.something_went_wrong)
        }
    }

    override fun onBackClicked() {
        emitEffect(effect = CheckoutEffect.NavigateBack)
    }

    override fun onConfirmOrderClicked() {
        tryToExecute(
            onStart = ::onConfirmOrderStart,
            block = ::onConfirmOrderBlock,
            onSuccess = ::onConfirmOrderSuccess,
            onError = ::onConfirmOrderError
        )
    }

    private fun onConfirmOrderStart() {
        updateState { copy(isTransactionLoading = true) }
    }

    private suspend fun onConfirmOrderBlock(): Transaction {
        return cartRepository.checkout(state.value.toDomain())
    }

    private fun onConfirmOrderSuccess(transaction: Transaction) {
        emitEffect(CheckoutEffect.NavigateToConfirmPayment(transaction.transactionId.toString()))
        updateState { copy(isTransactionLoading = false, isConfirmOrderButtonEnabled = true) }
    }

    private fun onConfirmOrderError(throwable: Throwable) {
        updateState { copy(isTransactionLoading = false, isConfirmOrderButtonEnabled = false) }
        when (throwable) {
            is NoInternetException -> showSnackBar(message = Res.string.no_internet_connection)
            else -> showSnackBar(message = Res.string.something_went_wrong)
        }
    }

    override fun onDismissSnackBar() {
        updateState { copy(snackBarState = null) }
    }

    override fun onChangeLocationClicked() {
        emitEffect(effect = CheckoutEffect.NavigateToChangeLocation)
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
}
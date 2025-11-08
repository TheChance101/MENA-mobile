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
import mena.dukan_presentation.generated.resources.no_internet_connection
import mena.dukan_presentation.generated.resources.something_went_wrong
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.exceptions.NoSuchItemException
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.service.LocationService
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val locationService: LocationService,
    private val savedStateHandle: SavedStateHandle,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseViewModel<CheckoutUiState, CheckoutEffect>(
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

    @OptIn(ExperimentalUuidApi::class)
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
        updateState { copy(deliveryAddress = activeAddress.toUiState()) }
    }

    private fun getActiveAddressError(throwable: Throwable) {
        when (throwable) {
            is NoInternetException -> showSnackBar(message = Res.string.no_internet_connection)
            else -> showSnackBar(message = Res.string.something_went_wrong)
        }
    }

    private fun updateTotalPrice() {
        tryToExecute(
            block = ::getTotalCartPrice,
            onSuccess = { onLoadCartSuccess(it.totalPrice) },
            onError = ::onCartInfoError,
        )
    }

    private suspend fun getTotalCartPrice(): Cart {
        val args = savedStateHandle.toRoute<DukanRoute.CheckoutScreenRoute>()
        return cartRepository.getCartInfo(args.dukanId)
    }

    private fun onLoadCartSuccess(totalPrice: Double) {
        updateState {
            copy(
                totalAmount = totalPrice,
            )
        }
    }

    private fun onCartInfoError(throwable: Throwable) {
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
        updateState { copy(isCheckoutImplementedDialogVisible = true) }
    }

    override fun onDismissCheckoutDialog() {
        updateState { copy(isCheckoutImplementedDialogVisible = false) }
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
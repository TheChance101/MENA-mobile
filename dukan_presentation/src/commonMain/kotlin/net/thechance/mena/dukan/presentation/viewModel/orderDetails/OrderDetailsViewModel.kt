@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.orderDetails

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.no_internet_connection
import mena.dukan_presentation.generated.resources.order_error_general
import mena.dukan_presentation.generated.resources.order_error_not_found
import mena.dukan_presentation.generated.resources.order_error_unauthorized_access
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.exceptions.NoSuchItemException
import net.thechance.mena.dukan.domain.exceptions.UnAuthorizedException
import net.thechance.mena.dukan.domain.repository.OrderRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class OrderDetailsViewModel(
    private val orderRepository: OrderRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : OrderDetailsInteractionListener,
    BaseViewModel<OrderDetailsUiState, OrderDetailsEffect>(
        initialState = OrderDetailsUiState(),
        defaultDispatcher = defaultDispatcher
    ) {

    fun loadOrderDetails(orderId: Uuid) {
        tryToExecute(
            onStart = ::onLoadOrderDetailsStart,
            block = { loadOrderDetailsBlock(orderId) },
            onSuccess = ::onLoadOrderDetailsSuccess,
            onError = ::onLoadOrderDetailsError,
        )
    }

    override fun onBackClicked() {
        emitEffect(OrderDetailsEffect.NavigateBack)
    }

    override fun onAddressDeliveryClicked(address: OrderDetailsUiState.AddressDeliveryUiState) {
        emitEffect(
            OrderDetailsEffect.NavigateToAddressOnMap(
                startLatitude = address.startLatitude,
                startLongitude = address.startLongitude,
                endLatitude = address.endLatitude,
                endLongitude = address.endLongitude
            )
        )
    }

    override fun onRetryLoadingOrderDetailsClicked(orderId: Uuid) {
        loadOrderDetails(orderId = orderId)
    }

    override fun onSnackBarDismissed() {
        updateState { copy(snackBarUiState = null) }
    }

    private fun onLoadOrderDetailsStart() {
        updateState {
            copy(orderDetailsScreenState = OrderDetailsUiState.OrderDetailsScreenState.Loading)
        }
    }

    private suspend fun loadOrderDetailsBlock(orderId: Uuid): OrderDetailsUiState.OrderUiState {
        return orderRepository.getOrderDetails(orderId).toUiState()
    }

    private fun onLoadOrderDetailsSuccess(orderUiState: OrderDetailsUiState.OrderUiState) {
        updateState {
            copy(
                orderDetailsScreenState = OrderDetailsUiState.OrderDetailsScreenState.Success,
                orderUiState = orderUiState
            )
        }
    }

    private fun onLoadOrderDetailsError(exception: Throwable) {
        updateState { copy(orderDetailsScreenState = OrderDetailsUiState.OrderDetailsScreenState.Error) }
        when (exception) {
            is NoInternetException -> showErrorSnackbar(resErrorMessage = Res.string.no_internet_connection)
            is UnAuthorizedException -> showErrorSnackbar(resErrorMessage = Res.string.order_error_unauthorized_access)
            is NoSuchItemException -> showErrorSnackbar(resErrorMessage = Res.string.order_error_not_found)
            else -> showErrorSnackbar(resErrorMessage = Res.string.order_error_general)
        }
    }


    private fun showErrorSnackbar(resErrorMessage: StringResource) {
        updateState {
            copy(
                snackBarUiState = SnackBarUiState(
                    message = resErrorMessage,
                    snackBarType = SnackBarType.ERROR
                )
            )
        }
    }
}
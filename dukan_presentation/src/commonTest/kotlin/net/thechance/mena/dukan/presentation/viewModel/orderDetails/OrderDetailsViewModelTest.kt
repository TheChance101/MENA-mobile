@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.orderDetails

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.exceptions.NoSuchItemException
import net.thechance.mena.dukan.domain.exceptions.UnAuthorizedException
import net.thechance.mena.dukan.domain.repository.OrderRepository
import net.thechance.mena.dukan.presentation.viewModel.orderDetails.OrderDetailsTest.createFakeOrderDetails
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class OrderDetailsViewModelTest {
    private val orderRepository: OrderRepository = mock(MockMode.autofill)
    private val orderId = Uuid.random()
    private lateinit var viewModel: OrderDetailsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OrderDetailsViewModel( orderId , orderRepository, testDispatcher)
    }

    @Test
    fun `loadOrderDetails should return orderDetails when success`() = runTest {
        everySuspend { orderRepository.getOrderDetails(orderId) } returns createFakeOrderDetails(orderId)

        val viewModel = createOrderViewModel()
        viewModel.state.test {
            skipItems(1)
            assertEquals(
                OrderDetailsUiState.OrderDetailsScreenState.Success,
                awaitItem().orderDetailsScreenState
            )
        }
    }

    @Test
    fun `loadOrderDetails should show NoInternet Content when NoInternetException happens`() =
        runTest {
            everySuspend { orderRepository.getOrderDetails(orderId) } throws NoInternetException()
            val viewModel = createOrderViewModel()
            viewModel.state.test {
                skipItems(2)
                assertEquals(
                    OrderDetailsUiState.OrderDetailsScreenState.NoInternet,
                    awaitItem().orderDetailsScreenState
                )
            }
        }

    @Test
    fun `loadOrderDetails should show No Order Found snack-bar Message when No order details with orderId`() =
        runTest {
            everySuspend { orderRepository.getOrderDetails(orderId) } throws NoSuchItemException()
            val viewModel = createOrderViewModel()
            viewModel.state.test {
                skipItems(2)
                assertNotNull(awaitItem().snackBarUiState)
            }
        }

    @Test
    fun `loadOrderDetails should show Unauthorized snack-bar Message when user not access to order details`() =
        runTest {
            everySuspend { orderRepository.getOrderDetails(orderId) } throws UnAuthorizedException()
            val viewModel = createOrderViewModel()
            viewModel.state.test {
                skipItems(2)
                assertNotNull(awaitItem().snackBarUiState)
            }
        }

    @Test
    fun `onBackClicked should  emit NavigateBack`() = runTest {
        viewModel.onBackClicked()

        viewModel.effect.test {
            val effect = awaitItem()
            assertEquals(OrderDetailsEffect.NavigateBack, effect)
        }
    }

    @Test
    fun `onAddressDeliveryClicked should emit NavigateToAddressOnMap`() = runTest {
        val addressUiState = OrderDetailsUiState.AddressDeliveryUiState(
            startLatitude = 37.7749,
            startLongitude = -122.4194,
            endLatitude = 37.7849,
            endLongitude = -122.4094
        )

        viewModel.onAddressDeliveryClicked(addressUiState)

        viewModel.effect.test {
            val effect = awaitItem()
            assertEquals(
                OrderDetailsEffect.NavigateToAddressOnMap(
                    startLatitude = 37.7749,
                    startLongitude = -122.4194,
                    endLatitude = 37.7849,
                    endLongitude = -122.4094
                ), effect
            )
        }
    }

    @Test
    fun `onRetryLoadingOrderDetailsClicked should call loadOrderDetailsAgain`() = runTest {
        everySuspend { orderRepository.getOrderDetails(orderId) } returns createFakeOrderDetails(orderId)

        viewModel.onRetryLoadingOrderDetailsClicked()
        advanceUntilIdle()

        assertEquals(
            OrderDetailsUiState.OrderDetailsScreenState.Success,
            viewModel.state.value.orderDetailsScreenState
        )
    }

    @Test
    fun `onSnackBarDismissed should dismiss snack-bar`() = runTest {
        viewModel.onSnackBarDismissed()

        assertNull(viewModel.state.value.snackBarUiState)
    }

    private fun createOrderViewModel(): OrderDetailsViewModel {
        return OrderDetailsViewModel(orderId, orderRepository, testDispatcher)
    }
}
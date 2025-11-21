@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.orderDetails

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.repository.OrderRepository
import net.thechance.mena.dukan.presentation.viewModel.orderDetails.OrderDetailsTest.createFakeOrderDetails
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class OrderDetailsViewModelTest {
    private val orderRepository: OrderRepository = mock(MockMode.autofill)
    private lateinit var viewModel: OrderDetailsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OrderDetailsViewModel(orderRepository, testDispatcher)
    }

    @Test
    fun `loadOrderDetails should return orderDetails when success`() = runTest {
        val orderId = Uuid.random()
        everySuspend { orderRepository.getOrderDetails(orderId) } returns createFakeOrderDetails(orderId)

        viewModel.loadOrderDetails(orderId)
        advanceUntilIdle()

        assertEquals(
            OrderDetailsUiState.OrderDetailsScreenState.Success,
            viewModel.state.value.orderDetailsScreenState
        )
    }

    @Test
    fun `loadOrderDetails should show ErrorSnack-bar with exception message when fails`() =
        runTest {
            val orderId = Uuid.random()
            everySuspend { orderRepository.getOrderDetails(orderId) } throws NoInternetException()

            viewModel.loadOrderDetails(orderId)
            advanceUntilIdle()

            assertEquals(
                OrderDetailsUiState.OrderDetailsScreenState.Error,
                viewModel.state.value.orderDetailsScreenState
            )
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
        val orderId = Uuid.random()

        viewModel.onRetryLoadingOrderDetailsClicked(orderId)

        verify { viewModel.loadOrderDetails(orderId) }
    }

    @Test
    fun `onSnackBarDismissed should dismiss snack-bar`() = runTest {
        viewModel.onSnackBarDismissed()

        assertNull(viewModel.state.value.snackBarUiState)
    }
}
@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.confirm_payment

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.wallet.domain.model.PaymentConfirmation
import net.thechance.mena.wallet.domain.repository.PaymentRepository
import net.thechance.mena.wallet.presentation.base.ErrorState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ConfirmPaymentViewModelTest {
    private val paymentRepository = mock<PaymentRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPaymentConfirmation should set state with loading when initially called`() =
        runTest {
            everySuspend { paymentRepository.getPaymentConfirmation(any(), any()) } returns paymentConfirmation1

            val viewModel = ConfirmPaymentViewModel(
                args = ConfirmPaymentArgs(receiver1Id.toString(), amount1),
                paymentRepository = paymentRepository,
                ioDispatcher = testDispatcher
            )

            viewModel.state.test {
                skipItems(1)
                val initialState = awaitItem()
                assertTrue(initialState.isLoading)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `ConfirmPaymentViewModel should update payment ui state when repository returns value`() =
        runTest {
            everySuspend { paymentRepository.getPaymentConfirmation(any(), any()) } returns paymentConfirmation1

            val viewModel = ConfirmPaymentViewModel(
                args = ConfirmPaymentArgs(receiver1Id.toString(), amount1),
                paymentRepository = paymentRepository,
                ioDispatcher = testDispatcher
            )

            viewModel.state.test {
                skipItems(2)
                val successState = awaitItem()
                assertEquals(
                    paymentUiState,
                    successState.paymentUiState
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `ConfirmPaymentViewModel should update error state when repository fails`() =
        runTest {
            val expectedError = Exception()
            everySuspend { paymentRepository.getPaymentConfirmation(any(), any()) } throws expectedError

            val viewModel = ConfirmPaymentViewModel(
                args = ConfirmPaymentArgs(receiver1Id.toString(), amount1),
                paymentRepository = paymentRepository,
                ioDispatcher = testDispatcher
            )

            viewModel.state.test {
                skipItems(2)
                val errorState = awaitItem()
                assertEquals(ErrorState.Unknown, errorState.errorState)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onBackButtonClicked should send NavigateBack effect`() = runTest {
        everySuspend { paymentRepository.getPaymentConfirmation(any(), any()) } returns paymentConfirmation1

        val viewModel = ConfirmPaymentViewModel(
            args = ConfirmPaymentArgs(receiver1Id.toString(), amount1),
            paymentRepository = paymentRepository,
            ioDispatcher = testDispatcher
        )

        viewModel.uiEffect.test {
            viewModel.onBackButtonClicked()
            assertEquals(ConfirmPaymentEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onRefresh should set state with loading when initially called`() = runTest {
        everySuspend { paymentRepository.getPaymentConfirmation(any(), any()) } returns paymentConfirmation1

        val viewModel = ConfirmPaymentViewModel(
            args = ConfirmPaymentArgs(receiver1Id.toString(), amount1),
            paymentRepository = paymentRepository,
            ioDispatcher = testDispatcher
        )

        viewModel.state.test {
            skipItems(3)
            viewModel.onRefresh()
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private companion object {
        val receiver1Id = Uuid.random()
        const val balance1 = 500045.0102
        const val amount1 = 5000.0

        const val balance1_ui = "500,045.0102"
        const val amount1_ui = "5,000"
        const val receiverName1 = "nour"
        val status1 = true
        const val receiverImg1 = "https://media.istockphoto.com/id/469738422/photo/large-boulders-on-lake-shore-at-sunset-minnesota-usa.jpg?s=612x612&w=0&k=20&c=4FzViDygZ8CgixTqt3VOudLJUP8uoSeh2UlD_qHYkAw="

        val paymentConfirmation1 = PaymentConfirmation(
            balance = balance1,
            receiverName = receiverName1,
            receiverImg = receiverImg1,
            status = status1
        )

        val paymentUiState = ConfirmPaymentScreenState.PaymentUiState(
            amount = amount1_ui,
            receiverName = receiverName1,
            receiverImage = receiverImg1,
            status = status1,
            balance = balance1_ui
        )
    }

}
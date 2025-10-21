@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.payment_result

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.screen.payment_result.args.PaymentResultArgs
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentResultViewModelTest {
    private val transactionRepository = mock<TransactionRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()

    private val transactionId1 = Uuid.random()
    private val receiverName1 = "user1"
    private val amount1 = 20.0
    private val paymentResultArgs = object : PaymentResultArgs {
        override val transactionId: String
            get() = transactionId1.toString()
        override val submitTransactionResultStatus: String
            get() = SubmissionStatus.SUCCESS.name
        override val receiverName: String
            get() = receiverName1
        override val amount: Double
            get() = amount1
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should set paymentStatus from args`() = runTest {
        val viewModel = PaymentResultViewModel(
            transactionRepository = transactionRepository,
            paymentResultArgs = paymentResultArgs,
            dispatcher = testDispatcher
        )

        assertEquals(
            SubmissionStatus.SUCCESS,
            viewModel.state.value.paymentStatus
        )
    }

    @Test
    fun `onBackClicked should send NavigateBack effect`() = runTest {
        val viewModel = PaymentResultViewModel(
            transactionRepository = transactionRepository,
            paymentResultArgs = paymentResultArgs,
            dispatcher = testDispatcher
        )

        viewModel.uiEffect.test {
            viewModel.onBackClicked()
            assertEquals(PaymentResultEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onCancelClicked should send NavigateToScreenBeforePaymentProcess effect`() = runTest {
        val viewModel = PaymentResultViewModel(
            transactionRepository = transactionRepository,
            paymentResultArgs = paymentResultArgs,
            dispatcher = testDispatcher
        )

        viewModel.uiEffect.test {
            viewModel.onCloseClicked()
            assertEquals(PaymentResultEffect.NavigateToPrePaymentScreen, awaitItem())
        }
    }

    @Test
    fun `onShowTransactionDetailsClicked should send NavigateToTransactionDetails effect with correct id`() = runTest {
        val viewModel = PaymentResultViewModel(
            transactionRepository = transactionRepository,
            paymentResultArgs = paymentResultArgs,
            dispatcher = testDispatcher
        )

        viewModel.uiEffect.test {
            viewModel.onShowTransactionDetailsClicked()
            assertEquals(
                PaymentResultEffect.NavigateToTransactionDetails(transactionId1),
                awaitItem()
            )
        }
    }

    @Test
    fun `onTryAgainClicked should update state with CONNECTION_LOST on error`() = runTest {
        everySuspend { transactionRepository.submitTransaction(transactionId1) } throws NoInternetException()

        val viewModel = PaymentResultViewModel(
            transactionRepository = transactionRepository,
            paymentResultArgs = paymentResultArgs,
            dispatcher = testDispatcher
        )

        viewModel.state.test {
            skipItems(1)
            viewModel.onTryAgainClicked()
            skipItems(2)
            val state = awaitItem()
            assertEquals(SubmissionStatus.CONNECTION_LOST, state.paymentStatus)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

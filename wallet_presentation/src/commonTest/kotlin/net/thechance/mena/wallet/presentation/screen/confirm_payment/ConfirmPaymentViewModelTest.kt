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
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.wallet.domain.model.TransactionReceiver
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.screen.confirm_payment.args.ConfirmPaymentArgs
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.screen.helper.FakeStringProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ConfirmPaymentViewModelTest {
    private val transactionRepository = mock<TransactionRepository>(mode = MockMode.autofill)
    private val balanceRepository = mock<BalanceRepository>(mode = MockMode.autofill)
    private val stringProvider = FakeStringProvider()
    private val testDispatcher = StandardTestDispatcher()
    private val confirmPaymentArgs: ConfirmPaymentArgs = object : ConfirmPaymentArgs {
        override val transactionId: String
            get() = receiver1Id.toString()
        override val amount: Double
            get() = amount1

    }

    private lateinit var viewModel: ConfirmPaymentViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = ConfirmPaymentViewModel(
        args = confirmPaymentArgs,
        balanceRepository = balanceRepository,
        transactionRepository = transactionRepository,
        stringProvider = stringProvider,
        dispatcher = testDispatcher
    )

    @Test
    fun `initial call sets loading state`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns balance1
        everySuspend { transactionRepository.getTransactionReceiver(receiver1Id) } returns transactionReceiver1

        viewModel = createViewModel()

        viewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            assertTrue(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ConfirmPaymentViewModel should update payment ui state when balance repository returns value`() =
        runTest {
            everySuspend { balanceRepository.getBalance() } returns balance1
            everySuspend { transactionRepository.getTransactionReceiver(receiver1Id) } returns transactionReceiver1

        viewModel = createViewModel()

            viewModel.state.test {
                skipItems(2)
                val successState = awaitItem()
                assertEquals(paymentUiState, successState.paymentUiState)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `ConfirmPaymentViewModel should update payment ui state when user repository returns value`() =
        runTest {
            everySuspend { balanceRepository.getBalance() } returns balance1
            everySuspend { transactionRepository.getTransactionReceiver(any()) } returns transactionReceiver1

        viewModel = createViewModel()

            viewModel.state.test {
                advanceUntilIdle()
                val successState = expectMostRecentItem()
                assertEquals(receiverUiState1, successState.receiverUiState)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `ConfirmPaymentViewModel should update error state when balance repository fails`() = runTest {
        val error = Exception()
        everySuspend { balanceRepository.getBalance() } throws error
        everySuspend { transactionRepository.getTransactionReceiver(receiver1Id) } returns transactionReceiver1

        viewModel = createViewModel()

        viewModel.state.test {
            skipItems(4)
            val errorState = awaitItem()
            assertEquals(ErrorState.UnknownError, errorState.errorState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ConfirmPaymentViewModel should update error state when user repository fails`() = runTest {
        val error = Exception()
        everySuspend { balanceRepository.getBalance() } returns balance1
        everySuspend { transactionRepository.getTransactionReceiver(receiver1Id) } throws error

        viewModel = createViewModel()

        viewModel.state.test {
            skipItems(5)
            val errorState = awaitItem()
            assertEquals(ErrorState.UnknownError, errorState.errorState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBackButtonClicked emits NavigateBack effect`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns balance1
        everySuspend { transactionRepository.getTransactionReceiver(receiver1Id) } returns transactionReceiver1

        viewModel = createViewModel()

        viewModel.uiEffect.test {
            viewModel.onBackButtonClicked()
            assertEquals(ConfirmPaymentEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onRefresh sets loading state`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns balance1
        everySuspend { transactionRepository.getTransactionReceiver(receiver1Id) } returns transactionReceiver1

        viewModel = createViewModel()

        viewModel.state.test {
            skipItems(6)
            viewModel.onRefresh()
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPayButtonClicked emits NavigateToPaymentResultScreen with success status`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns balance1
        everySuspend { transactionRepository.getTransactionReceiver(receiver1Id) } returns transactionReceiver1
        everySuspend { transactionRepository.submitTransaction(any()) } returns Unit

        viewModel = createViewModel()

        viewModel.uiEffect.test {
            viewModel.onPayButtonClicked()
            val effect = awaitItem()
            assertTrue(effect is ConfirmPaymentEffect.NavigateToPaymentResultScreen)
            assertEquals(SubmissionStatus.SUCCESS, effect.submissionStatus)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private companion object {
        val receiver1Id = Uuid.random()
        const val balance1 = 500045.2102
        const val amount1 = 5000.0

        const val balance1_ui = "500,045.21"
        const val amount1_ui = "5,000"
        const val receiverName1 = "nour"
        val status1 = true
        const val receiverImg1 = "https://media.istockphoto.com/id/469738422/photo/large-boulders-on-lake-shore-at-sunset-minnesota-usa.jpg?s=612x612&w=0&k=20&c=4FzViDygZ8CgixTqt3VOudLJUP8uoSeh2UlD_qHYkAw="

        val transactionReceiver1 = TransactionReceiver(
            name = receiverName1,
            imgUrl = receiverImg1
        )

        val receiverUiState1 = ConfirmPaymentScreenState.ReceiverUiState(
            name = receiverName1,
            profileImg = receiverImg1
        )

        val paymentUiState = ConfirmPaymentScreenState.PaymentUiState(
            amount = amount1_ui,
            status = status1,
            balance = balance1_ui
        )
    }
}
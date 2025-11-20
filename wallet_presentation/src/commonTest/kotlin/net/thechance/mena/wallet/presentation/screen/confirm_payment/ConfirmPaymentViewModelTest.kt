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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.confirm_payment_content_success
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.entity.TransactionStatus
import net.thechance.mena.wallet.domain.entity.TransactionType
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.screen.confirm_payment.args.ConfirmPaymentArgs
import net.thechance.mena.wallet.presentation.utils.StringProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ConfirmPaymentViewModelTest {
    private val transactionRepository = mock<TransactionRepository>(mode = MockMode.autofill)
    private val balanceRepository = mock<BalanceRepository>(mode = MockMode.autofill)
    private val stringProvider = mock<StringProvider>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private val confirmPaymentArgs: ConfirmPaymentArgs = object : ConfirmPaymentArgs {
        override val transactionId: String
            get() = receiver1Id.toString()
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
        everySuspend {
            transactionRepository.getTransactionById(receiver1Id)
        } returns transactionReceiver1

        viewModel = createViewModel()

        viewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            assertTrue(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ConfirmPaymentViewModel should update error state when balance repository fails`() =
        runTest {
            val error = Exception()
            everySuspend { balanceRepository.getBalance() } throws error
            everySuspend {
                transactionRepository.getTransactionById(receiver1Id)
            } returns transactionReceiver1

            viewModel = createViewModel()

            viewModel.state.test {
                skipItems(2)
                val errorState = awaitItem()
                assertEquals(ErrorState.UnknownError, errorState.errorState)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `ConfirmPaymentViewModel should update error state when user repository fails`() = runTest {
        val error = Exception()
        everySuspend { balanceRepository.getBalance() } returns balance1
        everySuspend {
            transactionRepository.getTransactionById(receiver1Id)
        } throws error

        viewModel = createViewModel()

        viewModel.state.test {
            skipItems(3)
            val errorState = awaitItem()
            assertEquals(ErrorState.UnknownError, errorState.errorState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBackButtonClicked emits NavigateBack effect`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns balance1
        everySuspend {
            transactionRepository.getTransactionById(receiver1Id)
        } returns transactionReceiver1

        viewModel = createViewModel()

        viewModel.uiEffect.test {
            viewModel.onBackButtonClicked()
            assertEquals(ConfirmPaymentEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onRefresh sets loading state`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns balance1
        everySuspend {
            transactionRepository.getTransactionById(receiver1Id)
        } returns transactionReceiver1

        viewModel = createViewModel()

        viewModel.state.test {
            skipItems(4)
            viewModel.onRefresh()
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPayButtonClicked emits NavigateToPaymentResultScreen with success status`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns balance1
        everySuspend {
            transactionRepository.getTransactionById(receiver1Id)
        } returns transactionReceiver1
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

    @Test
    fun `onPayButtonClicked emits NavigateToPaymentResultScreen with connection lost status`() =
        runTest {
            everySuspend { balanceRepository.getBalance() } returns balance1
            everySuspend {
                transactionRepository.getTransactionById(receiver1Id)
            } returns transactionReceiver1
            everySuspend {
                transactionRepository.submitTransaction(any())
            } throws Exception("No internet")

            viewModel = createViewModel()

            viewModel.uiEffect.test {
                viewModel.onPayButtonClicked()
                val effect = awaitItem()
                assertTrue(effect is ConfirmPaymentEffect.NavigateToPaymentResultScreen)
                assertEquals(SubmissionStatus.UNKNOWN_ERROR, effect.submissionStatus)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `updateUserMessage sets correct message when balance is insufficient`() = runTest {
        val lowBalance = 100.0
        everySuspend { balanceRepository.getBalance() } returns lowBalance
        everySuspend {
            transactionRepository.getTransactionById(receiver1Id)
        } returns transactionReceiver1
        everySuspend {
            stringProvider.getString(any(), any())
        } returns "Insufficient balance"

        viewModel = createViewModel()

        viewModel.state.test {
            skipItems(4)
            val state = awaitItem()
            assertEquals("Insufficient balance", state.userMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateUserMessage sets correct message when balance is sufficient`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns balance1
        everySuspend {
            transactionRepository.getTransactionById(receiver1Id)
        } returns transactionReceiver1
        everySuspend {
            stringProvider.getString(
                Res.string.confirm_payment_content_success, any()
            )
        } returns "Balance sufficient"

        viewModel = createViewModel()

        viewModel.state.test {
            skipItems(4)
            val state = awaitItem()
            assertEquals("Balance sufficient", state.userMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTransactionDetails updates state correctly`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns balance1
        everySuspend {
            transactionRepository.getTransactionById(receiver1Id)
        } returns transactionReceiver1

        viewModel = createViewModel()

        viewModel.state.test {
            skipItems(3)
            val state = awaitItem()
            assertEquals(transactionReceiver1.amount, state.amount)
            assertEquals(transactionReceiver1.receiverName, state.receiverUiState.name)
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
        const val receiverImg1 =
            "https://media.istockphoto.com/id/469738422/photo/large-boulders-on-lake-shore-at-sunset-minnesota-usa.jpg?s=612x612&w=0&k=20&c=4FzViDygZ8CgixTqt3VOudLJUP8uoSeh2UlD_qHYkAw="

        val transactionReceiver1 = createDefaultTransaction(
            receiverName = receiverName1,
            receiverImageUrl = receiverImg1,
            amount = amount1
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

        @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
        fun createDefaultTransaction(
            senderName: String = "Nour Elhoda",
            senderImageUrl: String? = "https://example.com/sender.jpg",
            receiverName: String,
            receiverImageUrl: String? = null,
            amount: Double = amount1
        ): Transaction {
            return Transaction(
                id = Uuid.random(),
                createdAt = Instant.parse("2025-08-20T12:00:00Z")
                    .toLocalDateTime(TimeZone.currentSystemDefault()),
                amount = amount,
                status = TransactionStatus.SUCCESS,
                senderName = senderName,
                senderImageUrl = senderImageUrl,
                receiverName = receiverName,
                receiverImageUrl = receiverImageUrl,
                type = TransactionType.SENT
            )
        }

    }
}
package net.thechance.mena.wallet.presentation.screen.transaction_history


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
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.UiState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class TransactionHistoryViewModelTest {
    private val transactionRepository = mock<TransactionRepository>(mode = MockMode.autofill)
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
    fun `getTransactionHistory should set history with loading when initially called`() = runTest {
        everySuspend { transactionRepository.getAll() } returns history
        val viewModel = TransactionHistoryViewModel(transactionRepository)

        viewModel.state.test {
            skipItems(1)
            val loadingState = awaitItem()
            assertTrue(loadingState.history is UiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTransactionHistory should update history with success when repository returns value`() = runTest {
        everySuspend { transactionRepository.getAll() } returns history
        val viewModel = TransactionHistoryViewModel(transactionRepository)
        advanceUntilIdle()

        viewModel.state.test {
            val successState = awaitItem()
            assertTrue(successState.history is UiState.Success)
            val transactions = (successState.history as UiState.Success<List<*>>).data
            assertEquals(history.size, transactions.size)
        }
    }

    @Test
    fun `getTransactionHistory should update history with error when repository throws exception`() = runTest {
        val expectedException = RuntimeException("test error")
        everySuspend { transactionRepository.getAll() } throws expectedException
        val viewModel = TransactionHistoryViewModel(transactionRepository)

        viewModel.state.test {
            skipItems(2)
            val errorState = awaitItem()
            assertTrue(errorState.history is UiState.Error)
            assertEquals(expectedException, (errorState.history).throwable)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should send NavigateBack effect when onBackClicked is called`() = runTest {
        val viewModel = TransactionHistoryViewModel(transactionRepository)
        viewModel.onBackClicked()

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertEquals(TransactionHistoryEffect.NavigateBack, effect)
        }
    }

    @Test
    fun `should send NavigateToTransactionDetails effect when onTransactionCardClicked is called`() = runTest {
        val viewModel = TransactionHistoryViewModel(transactionRepository)
        val id = Uuid.random()
        viewModel.onTransactionCardClicked(id)

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertEquals(TransactionHistoryEffect.NavigateToTransactionDetails(id), effect)
        }
    }

    @Test
    fun `should send NavigateToExportTransaction effect when onShareClicked is called`() = runTest {
        val viewModel = TransactionHistoryViewModel(transactionRepository)
        viewModel.onShareClicked()

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertEquals(TransactionHistoryEffect.NavigateToExportTransaction, effect)
        }
    }

    @Test
    fun `should send NavigateToFilterBottomSheet effect when onFilterClicked is called`() = runTest {
        val viewModel = TransactionHistoryViewModel(transactionRepository)
        viewModel.onFilterClicked()

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertEquals(TransactionHistoryEffect.NavigateToFilterBottomSheet, effect)
        }
    }


    @OptIn(ExperimentalUuidApi::class)
    companion object{
        val history = listOf(
            Transaction(
                id = Uuid.random(),
                createdAt = LocalDateTime(
                    date = LocalDate(2025, 8, 20),
                    time = LocalTime(12, 0)
                ),
                amount = 120.0,
                status = Transaction.Status.SUCCESS,
                senderId = Uuid.random(),
                senderName = "Alice",
                receiverId = Uuid.random(),
                receiverName = "Bob",
                type = Transaction.Type.SENT
            ),
            Transaction(
                id = Uuid.random(),
                createdAt = LocalDateTime(
                    date = LocalDate(2025, 8, 20),
                    time = LocalTime(12, 0)
                ),
                amount = 75.5,
                status = Transaction.Status.FAIL,
                senderId = Uuid.random(),
                senderName = "Charlie",
                receiverId = Uuid.random(),
                receiverName = "You",
                type = Transaction.Type.RECEIVED
            ),
            Transaction(
                id = Uuid.random(),
                createdAt = LocalDateTime(
                    date = LocalDate(2025, 8, 20),
                    time = LocalTime(12, 0)
                ),
                amount = 200.0,
                status = Transaction.Status.SUCCESS,
                senderId = Uuid.random(),
                senderName = "Online Shop",
                receiverId = Uuid.random(),
                receiverName = "You",
                type = Transaction.Type.ONLINE_PURCHASE
            )
        )
    }
}
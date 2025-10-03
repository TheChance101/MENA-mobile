package net.thechance.mena.wallet.presentation.screen.transaction_history

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
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
import net.thechance.mena.wallet.domain.model.TransactionStatus
import net.thechance.mena.wallet.domain.model.TransactionType
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun `state should not have error when repository returns value`() = runTest(testDispatcher) {
        everySuspend { transactionRepository.getTransactionHistory(null) } returns history
        val viewModel = TransactionHistoryViewModel(transactionRepository)
        viewModel.state.test {
            awaitItem()
            advanceUntilIdle()
            val successState = awaitItem()
            assertEquals(null, successState.isError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should send NavigateBack effect when onBackClicked is called`() = runTest(testDispatcher) {
        everySuspend { transactionRepository.getTransactionHistory(null) } returns emptyList()
        val viewModel = TransactionHistoryViewModel(transactionRepository)
        advanceUntilIdle()
        viewModel.uiEffect.test {
            viewModel.onBackClicked()
            val effect = awaitItem()
            assertEquals(TransactionHistoryEffect.NavigateBack, effect)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should send NavigateToTransactionDetails effect when onTransactionCardClicked is called`() = runTest(testDispatcher) {
        everySuspend { transactionRepository.getTransactionHistory(null) } returns emptyList()
        val viewModel = TransactionHistoryViewModel(transactionRepository)
        val id = Uuid.random()
        advanceUntilIdle()
        viewModel.uiEffect.test {
            viewModel.onTransactionCardClicked(id)
            val effect = awaitItem()
            assertEquals(TransactionHistoryEffect.NavigateToTransactionDetails(id), effect)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should send NavigateToExportTransaction effect when onExportClicked is called`() = runTest(testDispatcher) {
        everySuspend { transactionRepository.getTransactionHistory(null) } returns emptyList()
        val viewModel = TransactionHistoryViewModel(transactionRepository)
        advanceUntilIdle()
        viewModel.uiEffect.test {
            viewModel.onExportClicked()
            val effect = awaitItem()
            assertEquals(TransactionHistoryEffect.NavigateToExportTransaction, effect)
            cancelAndIgnoreRemainingEvents()
        }
    }

    companion object {
        val history = listOf(
            Transaction(
                id = Uuid.random(),
                createdAt = LocalDateTime(
                    date = LocalDate(2025, 8, 20),
                    time = LocalTime(12, 0)
                ),
                amount = 120.0,
                status = TransactionStatus.SUCCESS,
                senderName = "Alice",
                receiverName = "Bob",
                type = TransactionType.SENT
            ),
            Transaction(
                id = Uuid.random(),
                createdAt = LocalDateTime(
                    date = LocalDate(2025, 8, 20),
                    time = LocalTime(12, 0)
                ),
                amount = 75.5,
                status = TransactionStatus.FAILED,
                senderName = "Charlie",
                receiverName = "You",
                type = TransactionType.RECEIVED
            ),
            Transaction(
                id = Uuid.random(),
                createdAt = LocalDateTime(
                    date = LocalDate(2025, 8, 20),
                    time = LocalTime(12, 0)
                ),
                amount = 200.0,
                status = TransactionStatus.SUCCESS,
                senderName = "Online Shop",
                receiverName = "You",
                type = TransactionType.ONLINE_PURCHASE
            )
        )
    }
}
package net.thechance.mena.wallet.presentation.screen.transaction_history

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.entity.TransactionStatus
import net.thechance.mena.wallet.domain.entity.TransactionType
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.utils.StringProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class TransactionHistoryViewModelTest {
    private val stringProvider = mock<StringProvider>(mode = MockMode.autofill)
    private val transactionRepository = mock<TransactionRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: TransactionHistoryViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        everySuspend {
            transactionRepository.getTransactionHistory(
                any(),
                any(),
                any()
            )
        } returns history
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should send NavigateBack effect when onBackClicked is called`() = runTest(testDispatcher) {
        initViewModel()

        viewModel.uiEffect.test {
            viewModel.onBackClicked()

            val effect = awaitItem()
            assertTrue(effect is TransactionHistoryEffect.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should send NavigateToTransactionDetails effect when onTransactionCardClicked is called`() =
        runTest(testDispatcher) {
            initViewModel()
            val testTransactionId = history[0].id

            viewModel.uiEffect.test {
                viewModel.onTransactionCardClicked(testTransactionId)

                val effect = awaitItem()
                assertTrue(effect is TransactionHistoryEffect.NavigateToTransactionDetails)
                assertEquals(testTransactionId, effect.id)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should send NavigateToExportTransaction effect when onExportClicked is called`() =
        runTest(testDispatcher) {
            initViewModel()
            viewModel.uiEffect.test {
                viewModel.onExportClicked()

                val effect = awaitItem()
                assertTrue(effect is TransactionHistoryEffect.NavigateToExportTransaction)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `filter should be visible when onFilterClicked is called`() = runTest(testDispatcher) {
        initViewModel()

        viewModel.onFilterClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertTrue(currentState.isFilterVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDismissFilter should hide filter`() = runTest {
        initViewModel()

        viewModel.onDismissFilter()
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertFalse(currentState.isFilterVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `page should be reset when onResetFilters is called`() = runTest(testDispatcher) {
        initViewModel()

        viewModel.onFilterTypeSelected(FilterType.SENT)
        viewModel.onFilterStatusSelected(FilterStatus.SUCCESS)
        advanceUntilIdle()

        viewModel.onResetFilterClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertTrue(currentState.filterState.selectedTypes.isEmpty())
            assertEquals(FilterStatus.ALL, currentState.filterState.selectedStatus)
            assertEquals(null, currentState.filterState.startDate)
            assertEquals(null, currentState.filterState.endDate)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter should be applied when onApplyFilter is called`() = runTest(testDispatcher) {
        initViewModel()

        viewModel.onApplyFilterClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertTrue(currentState.isLoading || currentState.history.isNotEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `end date should be picked when onEndDateClicked is called`() = runTest(testDispatcher) {
        initViewModel()

        viewModel.onEndDateClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertTrue(currentState.filterState.isDateBottomSheetVisible)
            assertEquals(
                TransactionFilterState.DatePickerMode.END_DATE,
                currentState.filterState.datePickerMode
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `date picker should be dismissed when onDismissDatePicker is called`() =
        runTest(testDispatcher) {
            initViewModel()

            viewModel.onEndDateClicked()
            advanceUntilIdle()

            viewModel.onDismissDatePicker()
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertFalse(currentState.filterState.isDateBottomSheetVisible)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `date should be picked when onPickDateClicked is called`() = runTest(testDispatcher) {
        initViewModel()

        val testDate = LocalDate(2025, 8, 20)
        viewModel.onEndDateClicked()
        advanceUntilIdle()

        viewModel.onPickDateClicked(testDate)
        advanceUntilIdle()

        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals(testDate, currentState.filterState.endDate)
            assertFalse(currentState.filterState.isDateBottomSheetVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter type should be selected when selectFilterType is called`() =
        runTest(testDispatcher) {
            initViewModel()

            val filterType = FilterType.SENT
            viewModel.onFilterTypeSelected(filterType)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertTrue(currentState.filterState.selectedTypes.contains(filterType))
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `filter status should be selected when selectFilterStatus is called`() =
        runTest(testDispatcher) {
            initViewModel()

            val filterStatus = FilterStatus.SUCCESS
            viewModel.onFilterStatusSelected(filterStatus)
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(filterStatus, currentState.filterState.selectedStatus)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should load transactions when onNextPageRequested `() =
        runTest(testDispatcher) {
            initViewModel()

            viewModel.onNextPageRequested()
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertTrue(currentState.history.isNotEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `page should be reset when onRetryLoadTransactions is called`() =
        runTest(testDispatcher) {
            initViewModel()

            viewModel.onFilterTypeSelected(FilterType.SENT)
            viewModel.onFilterStatusSelected(FilterStatus.SUCCESS)
            advanceUntilIdle()

            viewModel.onRetryLoadTransactionHistoryClicked()
            advanceUntilIdle()

            viewModel.state.test {
                val currentState = awaitItem()
                assertTrue(currentState.history.isNotEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun TestScope.initViewModel() {
        viewModel = TransactionHistoryViewModel(
            transactionRepository = transactionRepository,
            stringProvider = stringProvider,
            dispatcher = testDispatcher
        )
        advanceUntilIdle()
    }

    private companion object {
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
                senderImageUrl = "",
                receiverImageUrl = "",
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
                senderImageUrl = "",
                receiverImageUrl = "",
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
                senderImageUrl = "",
                receiverImageUrl = "",
                type = TransactionType.ONLINE_PURCHASE
            ),
        )
    }
}
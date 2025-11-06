package net.thechance.mena.wallet.presentation.screen.export

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.IOException
import net.thechance.mena.wallet.domain.exceptions.NoDataFoundException
import net.thechance.mena.wallet.domain.model.StatementWithMetaData
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.StatementRepository
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.utils.FileManager
import net.thechance.mena.wallet.presentation.utils.StringProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
class ExportTransactionsViewModelTest {
    private val stringProvider = mock<StringProvider>(mode = MockMode.autofill)
    private val repository = mock<StatementRepository>(mode = MockMode.autofill)
    private val fileManager = mock<FileManager>(mode = MockMode.autofill)
    private val transactionRepository = mock<TransactionRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ExportTransactionsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun terDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should send NavigateBack effect when onBackClicked is called`() = runTest {
        initViewModel()
        viewModel.onBackClicked()

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is ExportTransactionsEffect.NavigateBack)
        }
    }

    @Test
    fun `should update state when onAllTransactionsClicked is called`() = runTest {
        initViewModel()
        viewModel.onCustomFilteringClicked()

        viewModel.state.test {
            skipItems(1)
            viewModel.onAllTransactionsClicked()

            val state = awaitItem()
            assertFalse(state.isCustomFilterCardSelected)
        }
    }

    @Test
    fun `should update state when onCustomFilteringClicked is called`() = runTest {
        initViewModel()

        viewModel.state.test {
            skipItems(1)
            viewModel.onCustomFilteringClicked()

            val state = awaitItem()
            assertTrue(state.isCustomFilterCardSelected)
        }
    }

    @Test
    fun `should toggle type in state when onTypeSelected is called`() = runTest {
        initViewModel()
        val type = FilterType.SENT

        viewModel.state.test {
            skipItems(1)
            viewModel.onTypeSelected(type)

            val stateWithType = awaitItem()
            assertTrue(stateWithType.filterState.selectedTransactionsTypes.contains(type))

            viewModel.onTypeSelected(type)
            val stateWithoutType = awaitItem()
            assertFalse(stateWithoutType.filterState.selectedTransactionsTypes.contains(type))
        }
    }

    @Test
    fun `onViewAndShareClicked with non-empty pdf should navigate`() = runTest {
        everySuspend { repository.getStatementWithMetadata(any()) } returns createMockStatementWithMetadata()

        initViewModel()

        viewModel.uiEffect.test {
            viewModel.onViewAndShareClicked()
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is ExportTransactionsEffect.NavigateToViewFileScreen)
        }
    }

    @Test
    fun `onViewAndShareClicked should fetch statement with custom filter when custom filter is selected`() =
        runTest {
            everySuspend { repository.getStatementWithMetadata(any()) } returns createMockStatementWithMetadata()

            initViewModel()

            viewModel.onCustomFilteringClicked()
            viewModel.onViewAndShareClicked()
            advanceUntilIdle()

            verifySuspend {
                repository.getStatementWithMetadata(
                    TransactionFilterParams(
                        emptyList(),
                        null,
                        null,
                        null
                    )
                )
            }
        }

    @Test
    fun `should update startDate when user picks start date`() = runTest {
        initViewModel()
        viewModel.state.test {
            skipItems(1)
            viewModel.onStartDateClicked()
            val state = awaitItem()
            assertTrue(state.dateState.isDateBottomSheetVisible)
            assertEquals(
                ExportTransactionsState.DatePickerMode.START_DATE,
                state.dateState.datePickerMode
            )
        }
    }

    @Test
    fun `should set defaultStartDate when current startDate exists`() = runTest {
        initViewModel()

        viewModel.state.test {
            skipItems(1)
            val existingStartDate = LocalDate(2024, 10, 15)
            viewModel.onPickDateClicked(LocalDate(2024, 10, 15))
            skipItems(1)
            viewModel.onStartDateClicked()
            val state = awaitItem()

            assertEquals(existingStartDate, state.dateState.defaultStartDate)
        }
    }

    @Test
    fun `should update endDate when  user picks end date`() = runTest {
        initViewModel()

        viewModel.state.test {
            skipItems(1)
            viewModel.onEndDateClicked()

            val state = awaitItem()
            assertTrue(state.dateState.isDateBottomSheetVisible)
            assertEquals(
                ExportTransactionsState.DatePickerMode.END_DATE,
                state.dateState.datePickerMode
            )
            assertNotNull(state.dateState.defaultEndDate)
        }
    }


    @Test
    fun `onViewAndShareClicked with generic error should show error snackBar`() = runTest {
        everySuspend {
            repository.getStatementWithMetadata(any())
        } throws Exception("Unknown")
        initViewModel()

        viewModel.state.test {
            viewModel.onViewAndShareClicked()
            skipItems(3)

            val state = awaitItem()
            assertSnackBarState(
                isVisible = true,
                snackBarState = state.snackBar
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDownloadClicked with non-empty pdf should show success snackBar`() = runTest {
        everySuspend { repository.getStatementWithMetadata(any()) } returns createMockStatementWithMetadata()
        everySuspend {
            fileManager.saveFile(any(), any(), any())
        } returns "MENA/statement_123.pdf"

        initViewModel()

        viewModel.state.test {
            viewModel.onDownloadClicked()
            skipItems(3)
            val state = awaitItem()

            assertSnackBarState(
                isVisible = true,
                snackBarState = state.snackBar
            )
            assertTrue(state.snackBar.isSuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDownloadClicked with generic error should show failure snackBar`() = runTest {
        everySuspend {
            repository.getStatementWithMetadata(any())
        } throws Exception("Unknown")

        initViewModel()

        viewModel.state.test {
            viewModel.onDownloadClicked()
            skipItems(3)

            val state = awaitItem()

            assertSnackBarState(
                isVisible = true,
                snackBarState = state.snackBar
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDownloadClicked with file save success should show success snackBar`() = runTest {
        everySuspend {
            repository.getStatementWithMetadata(any())
        } returns createMockStatementWithMetadata()
        everySuspend {
            fileManager.saveFile(any(), any(), any())
        } returns "MENA/statement_123.pdf"

        initViewModel()

        viewModel.state.test {
            viewModel.onDownloadClicked()
            advanceUntilIdle()
            skipItems(3)

            val state = awaitItem()
            assertSnackBarState(true, state.snackBar)
            assertTrue(state.snackBar.isSuccess)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDownloadClicked with file save error should show failure snackBar`() = runTest {
        everySuspend {
            repository.getStatementWithMetadata(any())
        } returns createMockStatementWithMetadata()
        everySuspend {
            fileManager.saveFile(any(), any(), any())
        } throws IOException()

        initViewModel()

        viewModel.state.test {
            viewModel.onDownloadClicked()
            advanceUntilIdle()
            skipItems(3)

            val state = awaitItem()
            assertSnackBarState(true, state.snackBar)
            assertFalse(state.snackBar.isSuccess)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toStartOfDayLocalDateTime should parse valid date string`() = runTest {
        val formatter =
            LocalDate.Format {
                year(); char('/'); monthNumber(); char('/');
                day(padding = Padding.ZERO)
            }
        val result = "2025/09/27".toStartOfDayLocalDateTime(formatter)

        assertEquals(2025, result?.year)
        assertEquals(9, result?.month?.number)
        assertEquals(27, result?.day)
    }

    @Test
    fun `toStartOfDayLocalDateTime should return null for empty string`() = runTest {
        val formatter =
            LocalDate.Format {
                year(); char('/'); monthNumber(); char('/');
                day(padding = Padding.ZERO)
            }
        val result = "".toStartOfDayLocalDateTime(formatter)
        assertEquals(null, result)
    }

    @Test
    fun whenDownloadThrowsNoDataFound_thenHasNoTransactionsErrorIsTrue_andToastShown() = runTest {
        everySuspend { repository.getStatementWithMetadata(any()) } throws NoDataFoundException()
        initViewModel()

        viewModel.state.test {
            viewModel.onViewAndShareClicked()
            skipItems(3)

            val state = awaitItem()
            assertTrue(state.hasNoTransactionsError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenDownloadFails_thenIsDownloadLoadingResetsToFalse() = runTest(testDispatcher) {
        everySuspend { repository.getStatementWithMetadata(any()) } throws RuntimeException("error")
        initViewModel()

        val state = viewModel.state.first()
        assertFalse(state.isDownloadLoading)
    }

    @Test
    fun whenViewAndShareFails_thenIsViewAndShareLoadingResetsToFalse() = runTest {
        everySuspend {
            repository.getStatementWithMetadata(any())
        } throws RuntimeException("error")
        initViewModel()

        viewModel.state.test {
            viewModel.onViewAndShareClicked()
            skipItems(4)

            val state = awaitItem()
            assertFalse(state.isViewAndShareLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenViewAndShareThrowsNoDataFound_thenHasNoTransactionsErrorIsTrue() = runTest {
        everySuspend {
            repository.getStatementWithMetadata(any())
        } throws NoDataFoundException()

        initViewModel()
        viewModel.state.test {
            viewModel.onViewAndShareClicked()
            skipItems(4)

            val state = awaitItem()
            assertTrue(state.hasNoTransactionsError)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenCustomFilterSelectedWithNoTypesOrDates_thenButtonsDisabled() = runTest {
        initViewModel()
        viewModel.state.test {
            viewModel.onCustomFilteringClicked()
            skipItems(1)

            val state = awaitItem()
            assertFalse(state.isDownloadButtonEnabled)
            assertFalse(state.isViewAndShareButtonEnabled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenViewAndShareSuccess_thenIsViewAndShareLoadingResetsToFalse() = runTest {
        everySuspend { repository.getStatementWithMetadata(any()) } returns createMockStatementWithMetadata()

        initViewModel()
        viewModel.state.test {
            viewModel.onViewAndShareClicked()
            skipItems(2)

            val state = awaitItem()
            assertFalse(state.isViewAndShareLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onViewAndShareClicked should reset view model`() = runTest {
        everySuspend { repository.getStatementWithMetadata(any()) } returns createMockStatementWithMetadata()
        initViewModel()
        advanceUntilIdle()

        viewModel.onCustomFilteringClicked()
        viewModel.onViewAndShareClicked()

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is ExportTransactionsEffect.NavigateToViewFileScreen)
        }
    }

    @Test
    fun whenDownloadSuccess_thenIsDownloadLoadingResetsToFalse() = runTest {
        everySuspend { repository.getStatementWithMetadata(any()) } returns createMockStatementWithMetadata()
        everySuspend {
            fileManager.saveFile(any(), any(), any())
        } returns "MENA/statement_123.pdf"

        initViewModel()
        viewModel.state.test {
            viewModel.onDownloadClicked()
            skipItems(2)

            val state = awaitItem()
            assertFalse(state.isDownloadLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenSelectedTransactionsTypesNotEmpty_thenHasActiveFiltersIsTrue() = runTest {
        val state = ExportTransactionsState(
            filterState =
                ExportTransactionsState.FilterState(
                    selectedTransactionsTypes = setOf(FilterType.SENT),
                    startDate = LocalDate(2025, 9, 1),
                    endDate = LocalDate(2025, 9, 30)
                )
        )
        assertTrue(state.hasActiveFilters)
    }

    @Test
    fun `onAllTransactionsClicked should enable download and view buttons`() = runTest {
        initViewModel()

        viewModel.state.test {
            viewModel.onAllTransactionsClicked()
            skipItems(0)

            val state = awaitItem()
            assertTrue(state.isDownloadButtonEnabled)
            assertTrue(state.isViewAndShareButtonEnabled)
            assertFalse(state.isCustomFilterCardSelected)

            cancelAndIgnoreRemainingEvents()
        }
    }

    fun `downloadPdf returns success with file path`() = runTest {
        everySuspend { repository.getStatementWithMetadata(any()) } returns createMockStatementWithMetadata()
        everySuspend {
            fileManager.saveFile(any(), any(), any())
        } returns "Downloads/MENA/statement_1234567890.pdf"

        initViewModel()

        viewModel.state.test {
            viewModel.onDownloadClicked()
            skipItems(5)

            val state = awaitItem()
            assertSnackBarState(true, state.snackBar)
            assertTrue(state.snackBar.isSuccess)
            assertNotNull(state.snackBar.message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun TestScope.initViewModel() {
        viewModel = ExportTransactionsViewModel(
            transactionRepository = transactionRepository,
            statementRepository = repository,
            fileManager = fileManager,
            dispatcher = testDispatcher,
            stringProvider = stringProvider
        )
        advanceUntilIdle()
    }

    private fun assertSnackBarState(
        isVisible: Boolean,
        snackBarState: SnackBarState
    ) {
        assertEquals(isVisible, snackBarState.isVisible)
    }

    private fun assertToastState(
        isVisible: Boolean,
        toastState: ExportTransactionsState.ToastState
    ) {
        assertEquals(isVisible, toastState.isVisible)
    }

    @OptIn(ExperimentalTime::class)
    private fun String?.toStartOfDayLocalDateTime(formatter: DateTimeFormat<LocalDate>):
            LocalDateTime? {
        return this
            ?.takeIf { it.isNotEmpty() }
            ?.let { LocalDate.parse(it, formatter) }
            ?.atStartOfDayIn(TimeZone.currentSystemDefault())
            ?.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    private fun createMockStatementWithMetadata(
        byteArray: ByteArray = byteArrayOf(0),
        startDate: LocalDate = LocalDate(2025, 9, 1),
        endDate: LocalDate = LocalDate(2025, 9, 30),
        totalInflows: Double = 1000.0,
        totalOutflows: Double = 500.0
    ): StatementWithMetaData {
        return StatementWithMetaData(
            byteArray = byteArray,
            startDate = startDate,
            endDate = endDate,
            totalInflows = totalInflows,
            totalOutflows = totalOutflows,
        )
    }

}
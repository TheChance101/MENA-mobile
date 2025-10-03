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
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.wallet.domain.exceptions.NoDataFoundException
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.StatementRepository
import net.thechance.mena.wallet.presentation.model.CustomToastState
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.screen.export.file_saver.FileSaver
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)

class ExportTransactionsViewModelTest {
    private val repository = mock<StatementRepository>(mode = MockMode.autofill)
    private val fileSaver = mock<FileSaver>(mode = MockMode.autofill)
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
            assertTrue(stateWithType.selectedTransactionsTypes!!.contains(type))

            viewModel.onTypeSelected(type)
            val stateWithoutType = awaitItem()
            assertFalse(stateWithoutType.selectedTransactionsTypes!!.contains(type))
        }
    }

    @Test
    fun `onViewAndShareClicked with non-empty pdf should navigate`() = runTest {
        everySuspend { repository.getTransactionsPdf(any()) } returns byteArrayOf(1, 2, 3)

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
            everySuspend { repository.getTransactionsPdf(any()) } returns byteArrayOf(1, 2, 3)

            initViewModel()

            viewModel.onCustomFilteringClicked()
            viewModel.onViewAndShareClicked()
            advanceUntilIdle()

            verifySuspend {
                repository.getTransactionsPdf(
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
    fun `should update startDate when onFromDateClicked is called`() = runTest {
        initViewModel()

        viewModel.state.test {
            skipItems(1)
            viewModel.onFromDateClicked()
            val state = awaitItem()
            assertEquals("2025/09/01", state.startDate)
        }
    }

    @Test
    fun `should update endDate when onToDateClicked is called`() = runTest {
        initViewModel()

        viewModel.state.test {
            skipItems(1)
            viewModel.onToDateClicked()
            val state = awaitItem()
            assertEquals("2025/09/27", state.endDate)
        }
    }


    @Test
    fun `onDownloadClicked with empty pdf should show toast`() = runTest {
        everySuspend { repository.getTransactionsPdf(any()) } returns byteArrayOf()

        initViewModel()

        viewModel.state.test {
            viewModel.onDownloadClicked()
            skipItems(2)

            val toastState = awaitItem().toast
            assertToastState(isVisible = true, toastState = toastState)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onViewAndShareClicked with generic error should show error snackBar`() = runTest {
        everySuspend {
            repository.getTransactionsPdf(any())
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
        everySuspend { repository.getTransactionsPdf(any()) } returns byteArrayOf(1, 2, 3)

        initViewModel()

        viewModel.state.test {
            viewModel.onDownloadClicked()
            skipItems(5)
            val state = awaitItem()

            assertSnackBarState(
                isVisible = true,
                snackBarState = state.snackBar
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDownloadClicked with NoInternetException should update noInternetConnection state`() =
        runTest {
            everySuspend {
                repository.getTransactionsPdf(any())
            } throws NoInternetException()

            initViewModel()

            viewModel.state.test {
                viewModel.onDownloadClicked()
                skipItems(5)

                val state = awaitItem()

                assertTrue(state.noInternetConnection)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onDownloadClicked with generic error should show failure snackBar`() = runTest {
        everySuspend {
            repository.getTransactionsPdf(any())
        } throws Exception("Unknown")

        initViewModel()

        viewModel.state.test {
            viewModel.onDownloadClicked()
            skipItems(5)

            val state = awaitItem()

            assertSnackBarState(
                isVisible = true,
                snackBarState = state.snackBar
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDownloadClicked with non-empty pdf and file saved should show success snackBar`() =
        runTest {
            everySuspend {
                repository.getTransactionsPdf(any())
            } returns byteArrayOf(1, 2, 3)
            everySuspend {
                fileSaver.saveFile(any(), any(), any())
            } returns true

            initViewModel()

            viewModel.state.test {
                viewModel.onDownloadClicked()
                advanceUntilIdle()
                skipItems(5)

                val state = awaitItem()
                assertSnackBarState(true, state.snackBar)
                assertTrue(state.snackBar.isSuccess)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onDownloadClicked with non-empty pdf but file not saved should show failure snackBar`() =
        runTest {
            everySuspend {
                repository.getTransactionsPdf(any())
            } returns byteArrayOf(1, 2, 3)
            everySuspend {
                fileSaver.saveFile(any(), any(), any())
            } returns false

            initViewModel()

            viewModel.state.test {
                viewModel.onDownloadClicked()
                advanceUntilIdle()
                skipItems(5)

                val state = awaitItem()
                assertSnackBarState(true, state.snackBar)
                assertFalse(state.snackBar.isSuccess)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onDownloadClicked with saveFile throwing exception should show failure snackBar`() =
        runTest {
            everySuspend {
                repository.getTransactionsPdf(any())
            } returns byteArrayOf(1, 2, 3)
            everySuspend {
                fileSaver.saveFile(any(), any(), any())
            } throws Exception("IO Error")

            initViewModel()

            viewModel.state.test {
                viewModel.onDownloadClicked()
                advanceUntilIdle()
                skipItems(5)

                val state = awaitItem()
                assertSnackBarState(true, state.snackBar)
                assertFalse(state.snackBar.isSuccess)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `toast should disappear after duration`() = runTest {

        initViewModel()

        viewModel.state.test {
            viewModel.onDownloadClicked()
            skipItems(2)

            val toastVisible = awaitItem().toast
            assertToastState(true, toastVisible)

            advanceTimeBy(2000L)
            val toastHidden = awaitItem().toast
            assertToastState(false, toastHidden)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `snackBar should disappear after duration`() = runTest {
        everySuspend {
            repository.getTransactionsPdf(any())
        } returns byteArrayOf(1, 2, 3)
        everySuspend {
            fileSaver.saveFile(any(), any(), any())
        } returns true

        initViewModel()

        viewModel.state.test {
            viewModel.onDownloadClicked()
            advanceUntilIdle()
            skipItems(5)

            val snackBarVisible = awaitItem().snackBar
            assertSnackBarState(true, snackBarVisible)

            advanceTimeBy(3000L)
            val snackBarHidden = awaitItem().snackBar
            assertSnackBarState(false, snackBarHidden)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onViewAndShareClicked with NoInternetException should update noInternetConnection`() =
        runTest {
            everySuspend { repository.getTransactionsPdf(any()) } throws NoInternetException()

            initViewModel()
            viewModel.state.test {
                viewModel.onViewAndShareClicked()
                skipItems(3)

                val state = awaitItem()
                assertTrue(state.noInternetConnection)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `toStartOfDayLocalDateTime should parse valid date string`() = runTest {
        val formatter =
            LocalDate.Format { year(); char('/'); monthNumber(); char('/'); dayOfMonth() }
        val result = "2025/09/27".toStartOfDayLocalDateTime(formatter)

        assertEquals(2025, result?.year)
        assertEquals(9, result?.monthNumber)
        assertEquals(27, result?.dayOfMonth)
    }

    @Test
    fun `toStartOfDayLocalDateTime should return null for empty string`() = runTest {
        val formatter =
            LocalDate.Format { year(); char('/'); monthNumber(); char('/'); dayOfMonth() }
        val result = "".toStartOfDayLocalDateTime(formatter)
        assertEquals(null, result)
    }

    @Test
    fun whenDownloadThrowsNoDataFound_thenHasNoTransactionsErrorIsTrue_andToastShown() = runTest {
        everySuspend { repository.getTransactionsPdf(any()) } throws NoDataFoundException()
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
        everySuspend { repository.getTransactionsPdf(any()) } throws RuntimeException("error")
        initViewModel()

        val state = viewModel.state.first()

        assertFalse(state.isDownloadLoading)
    }

    @Test
    fun whenViewAndShareFails_thenIsViewAndShareLoadingResetsToFalse() = runTest {
        everySuspend {
            repository.getTransactionsPdf(any())
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
    fun whenViewAndShareThrowsNoDataFound_thenHasNoTransactionsErrorIsTrue() =
        runTest {
            everySuspend {
                repository.getTransactionsPdf(any())
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
        everySuspend { repository.getTransactionsPdf(any()) } returns byteArrayOf(1, 2, 3)

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
        everySuspend { repository.getTransactionsPdf(any()) } returns byteArrayOf(1, 2, 3)
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
        everySuspend { repository.getTransactionsPdf(any()) } returns byteArrayOf(1, 2, 3)

        initViewModel()
        viewModel.state.test {
            viewModel.onDownloadClicked()
            skipItems(4)

            val state = awaitItem()
            assertFalse(state.isDownloadLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenSelectedTransactionsTypesNotEmpty_thenHasActiveFiltersIsTrue() = runTest {
        val state = ExportTransactionsState(
            selectedTransactionsTypes = setOf(FilterType.SENT)
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

    @Test
    fun `hideToast should hide toast`() = runTest {
        initViewModel()
        viewModel.state.test {
            viewModel.onDownloadClicked()
            skipItems(2)

            val toastVisible = awaitItem().toast
            assertTrue(toastVisible.isVisible)

            advanceTimeBy(2000L)

            val toastHidden = awaitItem().toast
            assertFalse(toastHidden.isVisible)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `hideSnackBar should hide snackbar after duration`() = runTest {
        everySuspend { repository.getTransactionsPdf(any()) } returns byteArrayOf(1, 2, 3)
        everySuspend { fileSaver.saveFile(any(), any(), any()) } returns true

        initViewModel()
        viewModel.state.test {
            viewModel.onDownloadClicked()
            skipItems(5)

            val visible = awaitItem().snackBar
            assertTrue(visible.isVisible)

            advanceTimeBy(3000L)

            val hidden = awaitItem().snackBar
            assertFalse(hidden.isVisible)

            cancelAndIgnoreRemainingEvents()
        }
    }


    private fun TestScope.initViewModel() {
        viewModel = ExportTransactionsViewModel(
            statementRepository = repository,
            fileSaver = fileSaver,
            ioDispatcher = testDispatcher
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
        toastState: CustomToastState
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
}
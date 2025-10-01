package net.thechance.mena.wallet.presentation.screen.export

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
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.repository.ExportTransactionsRepository
import net.thechance.mena.wallet.presentation.base.CustomToastState
import net.thechance.mena.wallet.presentation.base.SnackBarState
import net.thechance.mena.wallet.presentation.model.FilterStatus
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
    private val repository = mock<ExportTransactionsRepository>(mode = MockMode.autofill)
    private val fileSaver = mock<FileSaver>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()

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
        val viewModel =
            ExportTransactionsViewModel(
                exportTransactionsRepository = repository,
                fileSaver = fileSaver,
                ioDispatcher = testDispatcher
            )
        viewModel.onBackClicked()

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is ExportTransactionsEffect.NavigateBack)
        }
    }

    @Test
    fun `should update state when onAllTransactionsClicked is called`() = runTest {
        val viewModel =
            ExportTransactionsViewModel(
                exportTransactionsRepository = repository,
                fileSaver = fileSaver,
                ioDispatcher = testDispatcher
            )
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
        val viewModel =
            ExportTransactionsViewModel(
                exportTransactionsRepository = repository,
                fileSaver = fileSaver,
                ioDispatcher = testDispatcher
            )

        viewModel.state.test {
            skipItems(1)
            viewModel.onCustomFilteringClicked()

            val state = awaitItem()
            assertTrue(state.isCustomFilterCardSelected)
        }
    }

    @Test
    fun `should toggle type in state when onTypeSelected is called`() = runTest {
        val viewModel =
            ExportTransactionsViewModel(
                exportTransactionsRepository = repository,
                fileSaver = fileSaver,
                ioDispatcher = testDispatcher
            )
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
    fun `should update status in state when onStatusSelected is called`() = runTest {
        val viewModel =
            ExportTransactionsViewModel(
                exportTransactionsRepository = repository,
                fileSaver = fileSaver,
                ioDispatcher = testDispatcher
            )
        val status = FilterStatus.SUCCESS

        viewModel.state.test {
            skipItems(1)
            viewModel.onStatusSelected(status)

            val state = awaitItem()
            assertEquals(status, state.selectedTransactionsStatus)
        }
    }

    @Test
    fun `onViewAndShareClicked with non-empty pdf should navigate`() = runTest {
        everySuspend { repository.getFilteredTransactionsFile(any()) } returns byteArrayOf(1, 2, 3)

        val viewModel =
            ExportTransactionsViewModel(
                exportTransactionsRepository = repository,
                fileSaver = fileSaver,
                ioDispatcher = testDispatcher
            )

        viewModel.uiEffect.test {
            viewModel.onViewAndShareClicked()
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is ExportTransactionsEffect.NavigateToViewFileScreen)
        }
    }

    @Test
    fun `should update startDate when onFromDateClicked is called`() = runTest {
        val viewModel =
            ExportTransactionsViewModel(
                exportTransactionsRepository = repository,
                fileSaver = fileSaver,
                ioDispatcher = testDispatcher
            )

        viewModel.state.test {
            skipItems(1)
            viewModel.onFromDateClicked()
            val state = awaitItem()
            assertEquals("2025/09/01", state.startDate)
        }
    }

    @Test
    fun `should update endDate when onToDateClicked is called`() = runTest {
        val viewModel =
            ExportTransactionsViewModel(
                exportTransactionsRepository = repository,
                fileSaver = fileSaver,
                ioDispatcher = testDispatcher
            )

        viewModel.state.test {
            skipItems(1)
            viewModel.onToDateClicked()
            val state = awaitItem()
            assertEquals("2025/09/27", state.endDate)
        }
    }


    @Test
    fun `onDownloadClicked with empty pdf should show toast`() = runTest {
        everySuspend { repository.getFilteredTransactionsFile(any()) } returns byteArrayOf()

        val viewModel =
            ExportTransactionsViewModel(
                exportTransactionsRepository = repository,
                fileSaver = fileSaver,
                ioDispatcher = testDispatcher
            )

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
            repository.getFilteredTransactionsFile(any())
        } throws Exception("Unknown")
        val viewModel =
            ExportTransactionsViewModel(
                exportTransactionsRepository = repository,
                fileSaver = fileSaver,
                ioDispatcher = testDispatcher
            )

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
        everySuspend { repository.getFilteredTransactionsFile(any()) } returns byteArrayOf(1, 2, 3)

        val viewModel =
            ExportTransactionsViewModel(
                exportTransactionsRepository = repository,
                fileSaver = fileSaver,
                ioDispatcher = testDispatcher
            )

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
                repository.getFilteredTransactionsFile(any())
            } throws NoInternetException()

            val viewModel =
                ExportTransactionsViewModel(
                    exportTransactionsRepository = repository,
                    fileSaver = fileSaver,
                    ioDispatcher = testDispatcher
                )

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
            repository.getFilteredTransactionsFile(any())
        } throws Exception("Unknown")

        val viewModel =
            ExportTransactionsViewModel(
                exportTransactionsRepository = repository,
                fileSaver = fileSaver,
                ioDispatcher = testDispatcher
            )

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
                repository.getFilteredTransactionsFile(any())
            } returns byteArrayOf(1, 2, 3)
            everySuspend {
                fileSaver.saveFile(any(), any(), any())
            } returns true

            val viewModel =
                ExportTransactionsViewModel(
                    exportTransactionsRepository = repository,
                    fileSaver = fileSaver,
                    ioDispatcher = testDispatcher
                )

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
                repository.getFilteredTransactionsFile(any())
            } returns byteArrayOf(1, 2, 3)
            everySuspend {
                fileSaver.saveFile(any(), any(), any())
            } returns false

            val viewModel =
                ExportTransactionsViewModel(
                    exportTransactionsRepository = repository,
                    fileSaver = fileSaver,
                    ioDispatcher = testDispatcher
                )

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
                repository.getFilteredTransactionsFile(any())
            } returns byteArrayOf(1, 2, 3)
            everySuspend {
                fileSaver.saveFile(any(), any(), any())
            } throws Exception("IO Error")

            val viewModel =
                ExportTransactionsViewModel(
                    exportTransactionsRepository = repository,
                    fileSaver = fileSaver,
                    ioDispatcher = testDispatcher
                )

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

        val viewModel =
            ExportTransactionsViewModel(
                exportTransactionsRepository = repository,
                fileSaver = fileSaver,
                ioDispatcher = testDispatcher
            )

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
            repository.getFilteredTransactionsFile(any())
        } returns byteArrayOf(1, 2, 3)
        everySuspend {
            fileSaver.saveFile(any(), any(), any())
        } returns true

        val viewModel =
            ExportTransactionsViewModel(
                exportTransactionsRepository = repository,
                fileSaver = fileSaver,
                ioDispatcher = testDispatcher
            )

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
            everySuspend { repository.getFilteredTransactionsFile(any()) } throws NoInternetException()

            val viewModel = ExportTransactionsViewModel(repository, fileSaver, testDispatcher)

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
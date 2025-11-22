package net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.domain.usecase.SearchRecitersUseCase
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters.args.DownloadedRecitersArgs
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadedRecitersViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testViewModel: DownloadedRecitersViewModel
    private val quranRepository: QuranRepository = mock(mode = MockMode.autofill)
    private val surahArgs: DownloadedRecitersArgs = mock(mode = MockMode.autofill)
    private lateinit var searchRecitersUseCase: SearchRecitersUseCase

    @BeforeTest
    fun setup() {
        startKoin {
            modules(
                module {
                    single { mock<SnackbarHandler>(MockMode.autofill) }
                }
            )
        }

        testDispatcher = StandardTestDispatcher()

        searchRecitersUseCase = SearchRecitersUseCase()

        everySuspend { surahArgs.surahId } returns TEST_SURAH_ID
        everySuspend { quranRepository.getDefaultReciter() } returns flowOf(DEFAULT_RECITER_ID)
        everySuspend { quranRepository.getReciters() } returns dummyReciters
        everySuspend { quranRepository.isSurahAudioCached(TEST_SURAH_ID, any()) } returns true

        testViewModel = DownloadedRecitersViewModel(
            quranRepository = quranRepository,
            surahArgs = surahArgs,
            searchRecitersUseCase = searchRecitersUseCase,
            dispatcher = testDispatcher,
        )
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `init should load all reciters successfully`() = runTest {
        verifySuspend(exactly(1)) { quranRepository.getReciters() }
        assertEquals(dummyReciters.size, testViewModel.uiState.value.reciters.size)
    }

    @Test
    fun `init should check download status for all reciters`() = runTest {
        verifySuspend(exactly(dummyReciters.size)) {
            quranRepository.isSurahAudioCached(TEST_SURAH_ID, any())
        }
    }

    @Test
    fun `state should initialize with correct surahId from args`() = runTest {
        assertEquals(TEST_SURAH_ID, testViewModel.uiState.value.surahId)
    }

    @Test
    fun `state should initialize with empty query`() = runTest {
        assertEquals(EMPTY_STRING, testViewModel.uiState.value.query)
    }

    @Test
    fun `onBackClick should navigate back`() = runTest {
        testViewModel.uiEffect.test {
            testViewModel.onBackClick()
            val effect = awaitItem()
            assertTrue(effect is DownloadedRecitersEffect.NavigateBack)
        }
    }

    @Test
    fun `onQueryChange should update query in state`() = runTest {
        testViewModel.onQueryChange(TEST_QUERY)
        assertEquals(TEST_QUERY, testViewModel.uiState.value.query)
    }

    @Test
    fun `onQueryChange should filter reciters using usecase`() = runTest {
        testViewModel.onQueryChange(FILTER_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        val expected = dummyReciters.filter {
            it.name.contains(FILTER_QUERY, ignoreCase = true)
        }

        assertEquals(expected.size, testViewModel.uiState.value.reciters.size)
    }

    @Test
    fun `onQueryChange with empty string should restore all reciters`() = runTest {
        testViewModel.onQueryChange(FILTER_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.onClearQueryClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(dummyReciters.size, testViewModel.uiState.value.reciters.size)
    }

    @Test
    fun `onQueryChange should trigger repository getReciters`() = runTest {
        val callsBefore = 1
        testViewModel.onQueryChange(FILTER_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(exactly(callsBefore + 1)) { quranRepository.getReciters() }
    }

    @Test
    fun `onClearQueryClick should clear query`() = runTest {
        testViewModel.onQueryChange(TEST_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.onClearQueryClick()

        assertEquals(EMPTY_STRING, testViewModel.uiState.value.query)
    }

    @Test
    fun `onSelectReciterClick should update selected reciter`() = runTest {
        everySuspend { quranRepository.saveDefaultReciter(any()) } returns Unit

        testViewModel.uiState.test {
            testViewModel.onSelectReciterClick(SELECTED_RECITER_ID)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            val updated = awaitItem()
            assertEquals(SELECTED_RECITER_ID, updated.reciterId)
        }
    }

    @Test
    fun `reciters should be marked as downloaded when cached`() = runTest {
        everySuspend {
            quranRepository.isSurahAudioCached(TEST_SURAH_ID, DOWNLOADED_RECITER_ID)
        } returns true

        testViewModel = DownloadedRecitersViewModel(
            quranRepository = quranRepository,
            surahArgs = surahArgs,
            searchRecitersUseCase = searchRecitersUseCase,
            dispatcher = testDispatcher,
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val downloaded = testViewModel.uiState.value.reciters
            .find { it.id == DOWNLOADED_RECITER_ID }

        assertTrue(downloaded?.isDownloaded ?: false)
    }

    @Test
    fun `onDeleteReciterAudioClick should show delete dialog`() = runTest {
        testViewModel.onDeleteReciterAudioClick(1)
        assertTrue(testViewModel.uiState.value.isDeleteConfirmationDialogVisible)
    }

    @Test
    fun `onDismissDeleteDialog should hide delete dialog`() = runTest {
        testViewModel.onDeleteReciterAudioClick(1)
        testViewModel.onDismissDeleteDialog()

        assertEquals(false, testViewModel.uiState.value.isDeleteConfirmationDialogVisible)
    }

    @Test
    fun `multiple filter operations should work correctly`() = runTest {
        testViewModel.onQueryChange(ABDUL_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()
        val size1 = testViewModel.uiState.value.reciters.size

        testViewModel.onQueryChange(BASIT_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()
        val size2 = testViewModel.uiState.value.reciters.size

        testViewModel.onClearQueryClick()
        testDispatcher.scheduler.advanceUntilIdle()
        val sizeAll = testViewModel.uiState.value.reciters.size

        assertTrue(size2 <= size1)
        assertEquals(dummyReciters.size, sizeAll)
    }

    private companion object {
        const val TEST_SURAH_ID = 1
        const val DEFAULT_RECITER_ID = 1
        const val SELECTED_RECITER_ID = 2
        const val DOWNLOADED_RECITER_ID = 1
        const val TEST_QUERY = "Abdul"
        const val FILTER_QUERY = "Abdul"
        const val ABDUL_QUERY = "Abdul"
        const val BASIT_QUERY = "Basit"
        const val EMPTY_STRING = ""

        private val dummyReciters = listOf(
            Reciter(1, "Abdul Basit Abdul Samad", "عبد الباسط عبد الصمد", "Murattal"),
            Reciter(2, "Mahmoud Khalil Al-Hussary", "محمود خليل الحصري", "Murattal"),
            Reciter(3, "Mishary Rashid Alafasy", "مشاري بن راشد العفاسي", "Murattal"),
            Reciter(4, "Abdul Rahman Al-Sudais", "عبد الرحمن السديس", "Murattal")
        )
    }
}
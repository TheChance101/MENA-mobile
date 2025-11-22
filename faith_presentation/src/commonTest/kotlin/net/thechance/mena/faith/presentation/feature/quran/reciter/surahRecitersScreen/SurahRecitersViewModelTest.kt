package net.thechance.mena.faith.presentation.feature.quran.reciter.surahRecitersScreen

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.atLeast
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.domain.mediaPlayer.QuranPlayer
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.domain.service.DownloadSurahManager
import net.thechance.mena.faith.domain.usecase.SearchRecitersUseCase
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import net.thechance.mena.faith.presentation.feature.quran.reciter.surahRecitersScreen.args.SurahRecitersArgs
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SurahRecitersViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testViewModel: SurahRecitersViewModel
    private val player: QuranPlayer = mock(mode = MockMode.autofill)
    private val quranRepository: QuranRepository = mock(mode = MockMode.autofill)
    private val downloadManager: DownloadSurahManager = mock(mode = MockMode.autofill)
    private lateinit var searchRecitersUseCase: SearchRecitersUseCase
    private val surahArgs: SurahRecitersArgs = mock(mode = MockMode.autofill)

    @BeforeTest
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        searchRecitersUseCase = SearchRecitersUseCase()

        everySuspend { surahArgs.surahId } returns TEST_SURAH_ID
        everySuspend { quranRepository.getDefaultReciter() } returns flowOf(DEFAULT_RECITER_ID)
        everySuspend { quranRepository.getReciters() } returns dummyReciters
        everySuspend { quranRepository.isSurahAudioCached(TEST_SURAH_ID, any()) } returns false

        startKoin {
            modules(
                module {
                    single { mock<SnackbarHandler>(MockMode.autofill) }
                }
            )
        }

        testViewModel = SurahRecitersViewModel(
            quranRepository = quranRepository,
            surahArgs = surahArgs,
            downloadManager = downloadManager,
            searchRecitersUseCase = searchRecitersUseCase,
            dispatcher = testDispatcher,
            quranPlayer = player
        )
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `init should load all reciters successfully`() = runTest {
        verifySuspend(atLeast(1)) { quranRepository.getReciters() }
        assertEquals(dummyReciters.size, testViewModel.uiState.value.reciters.size)
    }

    @Test
    fun `init should set default reciter from repository`() = runTest {
        assertEquals(DEFAULT_RECITER_ID, testViewModel.uiState.value.selectedReciterId)
    }

    @Test
    fun `state should initialize with correct surahId from args`() = runTest {
        assertEquals(TEST_SURAH_ID, testViewModel.uiState.value.surahId)
    }

    @Test
    fun `onQueryChange should update query in state`() = runTest {
        testViewModel.onQueryChange(TEST_QUERY)

        assertEquals(TEST_QUERY, testViewModel.uiState.value.query)
    }

    @Test
    fun `onQueryChange should trigger search with use case`() = runTest {
        testViewModel.onQueryChange(FILTER_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        val expectedResults = dummyReciters.filter {
            it.name.contains(FILTER_QUERY, ignoreCase = true)
        }
        assertEquals(expectedResults.size, testViewModel.uiState.value.reciters.size)
    }

    @Test
    fun `onQueryChange with empty query should show all reciters`() = runTest {
        testViewModel.onQueryChange(FILTER_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.onQueryChange(EMPTY_STRING)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(dummyReciters.size, testViewModel.uiState.value.reciters.size)
    }

    @Test
    fun `onQueryChange with blank query should trigger search`() = runTest {
        testViewModel.onQueryChange(FILTER_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.onQueryChange(BLANK_STRING)
        testDispatcher.scheduler.advanceUntilIdle()

        // Blank query should return all reciters
        assertEquals(dummyReciters.size, testViewModel.uiState.value.reciters.size)
    }

    @Test
    fun `onClearQueryClick should clear query`() = runTest {
        testViewModel.onQueryChange(TEST_QUERY)

        testViewModel.onClearQueryClick()

        assertEquals(EMPTY_STRING, testViewModel.uiState.value.query)
    }

    @Test
    fun `onClearQueryClick should reload all reciters`() = runTest {
        testViewModel.onQueryChange(FILTER_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.onClearQueryClick()
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(atLeast(2)) { quranRepository.getReciters() }
        assertEquals(dummyReciters.size, testViewModel.uiState.value.reciters.size)
    }

    @Test
    fun `onBackClick should navigate back`() = runTest {
        testViewModel.uiEffect.test {
            testViewModel.onBackClick()

            val effect = awaitItem()
            assertTrue(effect is SurahRecitersScreenEffect.NavigateBack)
        }
    }


    @Test
    fun `onDownloadClick should get remote url from repository`() = runTest {
        everySuspend {
            quranRepository.getRemoteSurahSoundUrl(TEST_SURAH_ID, TEST_RECITER_ID)
        } returns REMOTE_URL
        everySuspend {
            downloadManager.downloadSurahFile(REMOTE_URL, TEST_SURAH_ID, TEST_RECITER_ID)
        } returns LOCAL_PATH
        everySuspend {
            quranRepository.saveSurahAudioToCache(TEST_SURAH_ID, TEST_RECITER_ID, LOCAL_PATH)
        } returns Unit
        everySuspend {
            quranRepository.isSurahAudioCached(TEST_SURAH_ID, TEST_RECITER_ID)
        } returns true

        testViewModel.onDownloadClick(TEST_RECITER_ID)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(exactly(1)) {
            quranRepository.getRemoteSurahSoundUrl(TEST_SURAH_ID, TEST_RECITER_ID)
        }
    }

    @Test
    fun `onDownloadClick should call download manager`() = runTest {
        everySuspend {
            quranRepository.getRemoteSurahSoundUrl(TEST_SURAH_ID, TEST_RECITER_ID)
        } returns REMOTE_URL
        everySuspend {
            downloadManager.downloadSurahFile(REMOTE_URL, TEST_SURAH_ID, TEST_RECITER_ID)
        } returns LOCAL_PATH
        everySuspend {
            quranRepository.saveSurahAudioToCache(TEST_SURAH_ID, TEST_RECITER_ID, LOCAL_PATH)
        } returns Unit
        everySuspend {
            quranRepository.isSurahAudioCached(TEST_SURAH_ID, TEST_RECITER_ID)
        } returns true

        testViewModel.onDownloadClick(TEST_RECITER_ID)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(exactly(1)) {
            downloadManager.downloadSurahFile(REMOTE_URL, TEST_SURAH_ID, TEST_RECITER_ID)
        }
    }

    @Test
    fun `onDownloadClick should save audio to cache`() = runTest {
        everySuspend {
            quranRepository.getRemoteSurahSoundUrl(TEST_SURAH_ID, TEST_RECITER_ID)
        } returns REMOTE_URL
        everySuspend {
            downloadManager.downloadSurahFile(REMOTE_URL, TEST_SURAH_ID, TEST_RECITER_ID)
        } returns LOCAL_PATH
        everySuspend {
            quranRepository.saveSurahAudioToCache(TEST_SURAH_ID, TEST_RECITER_ID, LOCAL_PATH)
        } returns Unit
        everySuspend {
            quranRepository.isSurahAudioCached(TEST_SURAH_ID, TEST_RECITER_ID)
        } returns true

        testViewModel.onDownloadClick(TEST_RECITER_ID)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(exactly(1)) {
            quranRepository.saveSurahAudioToCache(TEST_SURAH_ID, TEST_RECITER_ID, LOCAL_PATH)
        }
    }

    @Test
    fun `onDownloadClick should update reciter download status in state`() = runTest {
        everySuspend {
            quranRepository.getRemoteSurahSoundUrl(TEST_SURAH_ID, TEST_RECITER_ID)
        } returns REMOTE_URL
        everySuspend {
            downloadManager.downloadSurahFile(REMOTE_URL, TEST_SURAH_ID, TEST_RECITER_ID)
        } returns LOCAL_PATH
        everySuspend {
            quranRepository.saveSurahAudioToCache(TEST_SURAH_ID, TEST_RECITER_ID, LOCAL_PATH)
        } returns Unit
        everySuspend {
            quranRepository.isSurahAudioCached(TEST_SURAH_ID, TEST_RECITER_ID)
        } returns true

        testViewModel.onDownloadClick(TEST_RECITER_ID)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedReciter = testViewModel.uiState.value.reciters.find { it.id == TEST_RECITER_ID }
        assertTrue(updatedReciter?.isDownloaded ?: false)
    }

    @Test
    fun `download should not affect other reciters download status`() = runTest {
        everySuspend {
            quranRepository.getRemoteSurahSoundUrl(TEST_SURAH_ID, TEST_RECITER_ID)
        } returns REMOTE_URL
        everySuspend {
            downloadManager.downloadSurahFile(REMOTE_URL, TEST_SURAH_ID, TEST_RECITER_ID)
        } returns LOCAL_PATH
        everySuspend {
            quranRepository.saveSurahAudioToCache(TEST_SURAH_ID, TEST_RECITER_ID, LOCAL_PATH)
        } returns Unit
        everySuspend {
            quranRepository.isSurahAudioCached(TEST_SURAH_ID, TEST_RECITER_ID)
        } returns true

        testViewModel.onDownloadClick(TEST_RECITER_ID)
        testDispatcher.scheduler.advanceUntilIdle()

        val otherReciters = testViewModel.uiState.value.reciters.filter { it.id != TEST_RECITER_ID }
        assertFalse(otherReciters.any { it.isDownloaded })
    }

    @Test
    fun `reciters should be marked as downloaded when cached`() = runTest {
        everySuspend {
            quranRepository.isSurahAudioCached(TEST_SURAH_ID, DOWNLOADED_RECITER_ID)
        } returns true
        everySuspend { quranRepository.getReciters() } returns dummyReciters

        testViewModel = SurahRecitersViewModel(
            quranRepository = quranRepository,
            surahArgs = surahArgs,
            downloadManager = downloadManager,
            searchRecitersUseCase = searchRecitersUseCase,
            dispatcher = testDispatcher,
            quranPlayer = player
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val downloadedReciter = testViewModel.uiState.value.reciters
            .find { it.id == DOWNLOADED_RECITER_ID }
        assertTrue(downloadedReciter?.isDownloaded ?: false)
    }

    @Test
    fun `filtered results should maintain download status`() = runTest {
        everySuspend {
            quranRepository.isSurahAudioCached(TEST_SURAH_ID, DOWNLOADED_RECITER_ID)
        } returns true

        testViewModel = SurahRecitersViewModel(
            quranRepository = quranRepository,
            surahArgs = surahArgs,
            downloadManager = downloadManager,
            searchRecitersUseCase = searchRecitersUseCase,
            dispatcher = testDispatcher,
            quranPlayer = player
        )
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.onQueryChange(FILTER_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        val filteredReciter = testViewModel.uiState.value.reciters
            .find { it.id == DOWNLOADED_RECITER_ID }
        assertTrue(filteredReciter?.isDownloaded ?: false)
    }

    @Test
    fun `search use case should be called with correct parameters`() = runTest {
        testViewModel.onQueryChange(TEST_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        val expectedResults = dummyReciters.filter {
            it.name.contains(TEST_QUERY, ignoreCase = true)
        }
        assertEquals(expectedResults.size, testViewModel.uiState.value.reciters.size)
    }

    @Test
    fun `empty search results should update state correctly`() = runTest {
        val noMatchQuery = "XYZ123"
        testViewModel.onQueryChange(noMatchQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(testViewModel.uiState.value.reciters.isEmpty())
    }

    private companion object {
        const val TEST_SURAH_ID = 1
        const val DEFAULT_RECITER_ID = 1
        const val TEST_RECITER_ID = 1
        const val DOWNLOADED_RECITER_ID = 1
        const val TEST_QUERY = "Abdul"
        const val FILTER_QUERY = "Abdul"
        const val EMPTY_STRING = ""
        const val BLANK_STRING = "   "
        const val REMOTE_URL = "https://example.com/surah.mp3"
        const val LOCAL_PATH = "/local/path/surah.mp3"

        private val dummyReciters = listOf(
            Reciter(
                id = 1,
                name = "Abdul Basit Abdul Samad",
                arabicName = "عبد الباسط عبد الصمد",
                tilawahType = "Murattal"
            ),
            Reciter(
                id = 2,
                name = "Mahmoud Khalil Al-Hussary",
                arabicName = "محمود خليل الحصري",
                tilawahType = "Murattal"
            ),
            Reciter(
                id = 3,
                name = "Mishary Rashid Alafasy",
                arabicName = "مشاري بن راشد العفاسي",
                tilawahType = "Murattal"
            )
        )
    }
}
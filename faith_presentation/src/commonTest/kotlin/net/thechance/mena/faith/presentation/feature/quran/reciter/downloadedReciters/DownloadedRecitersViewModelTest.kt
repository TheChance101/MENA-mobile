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

    @BeforeTest
    fun setup() {
        startKoin {
            modules(module {
                    single { mock<SnackbarHandler>(MockMode.autofill) }
                }
            )
        }

        testDispatcher = StandardTestDispatcher()
        everySuspend { surahArgs.surahId } returns TEST_SURAH_ID
        everySuspend { surahArgs.isSwipeToDeleteEnabled } returns true
        everySuspend { quranRepository.getDefaultReciter() } returns flowOf(DEFAULT_RECITER_ID)
        everySuspend { quranRepository.getReciters() } returns dummyReciters
        everySuspend { quranRepository.isSurahAudioCached(TEST_SURAH_ID, any()) } returns true

        testViewModel = DownloadedRecitersViewModel(
            quranRepository = quranRepository,
            surahArgs = surahArgs,
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
        assertEquals(dummyReciters.size, testViewModel.uiState.value.reciters.size)
    }

    @Test
    fun `init should set default reciter from repository`() = runTest {
        assertEquals(DEFAULT_RECITER_ID, testViewModel.uiState.value.selectedReciterId)
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
    fun `state should initialize with isSwipeable from args`() = runTest {
        assertTrue(testViewModel.uiState.value.isSwipeable)
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
    fun `onQueryChange should filter reciters by name case insensitive`() = runTest {
        testViewModel.onQueryChange(FILTER_QUERY)

        val filteredReciters = testViewModel.uiState.value.reciters
        assertTrue(filteredReciters.size < dummyReciters.size)
        assertTrue(filteredReciters.all { it.name.contains(FILTER_QUERY, ignoreCase = true) })
    }

    @Test
    fun `onQueryChange with empty string should show all reciters`() = runTest {
        testViewModel.onQueryChange(FILTER_QUERY)
        testViewModel.onQueryChange(EMPTY_STRING)

        assertEquals(dummyReciters.size, testViewModel.uiState.value.reciters.size)
    }

    @Test
    fun `onQueryChange with blank string should show all reciters`() = runTest {
        testViewModel.onQueryChange(FILTER_QUERY)
        testViewModel.onQueryChange(BLANK_STRING)

        assertEquals(dummyReciters.size, testViewModel.uiState.value.reciters.size)
    }

    @Test
    fun `onQueryChange should not modify allReciters`() = runTest {
        val allRecitersBefore = testViewModel.allReciters

        testViewModel.onQueryChange(FILTER_QUERY)

        assertEquals(allRecitersBefore, testViewModel.allReciters)
    }

    @Test
    fun `onQueryChange with no matches should return empty list`() = runTest {
        testViewModel.onQueryChange(NO_MATCH_QUERY)

        assertTrue(testViewModel.uiState.value.reciters.isEmpty())
    }

    @Test
    fun `onQueryChange should perform local search without repository call`() = runTest {
        val initialCallCount = 1 // من الـ init
        testViewModel.onQueryChange(FILTER_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(exactly(initialCallCount)) { quranRepository.getReciters() }
    }

    @Test
    fun `onClearQueryClick should clear query`() = runTest {
        testViewModel.onQueryChange(TEST_QUERY)

        testViewModel.onClearQueryClick()

        assertEquals(EMPTY_STRING, testViewModel.uiState.value.query)
    }

    @Test
    fun `onClearQueryClick should restore all reciters`() = runTest {
        val allRecitersBeforeFilter = testViewModel.allReciters

        testViewModel.onQueryChange(FILTER_QUERY)

        testViewModel.onClearQueryClick()

        assertEquals(allRecitersBeforeFilter, testViewModel.uiState.value.reciters)
        assertEquals(allRecitersBeforeFilter.size, testViewModel.uiState.value.reciters.size)
    }

    @Test
    fun `onClearQueryClick after filtering should show all reciters`() = runTest {
        testViewModel.onQueryChange(FILTER_QUERY)
        assertTrue(testViewModel.uiState.value.reciters.size < dummyReciters.size)

        testViewModel.onClearQueryClick()

        assertEquals(dummyReciters.size, testViewModel.uiState.value.reciters.size)
    }


    @Test
    fun `onSelectReciterClick should update selected reciter in state`() = runTest {
        everySuspend { quranRepository.saveDefaultReciter(any()) } returns Unit

        testViewModel.uiState.test {
            testViewModel.onSelectReciterClick(SELECTED_RECITER_ID)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)
            val updatedState = awaitItem()
            assertEquals(SELECTED_RECITER_ID, updatedState.selectedReciterId)
        }
    }

    @Test
    fun `reciters should be marked as downloaded when cached`() = runTest {
        everySuspend {
            quranRepository.isSurahAudioCached(TEST_SURAH_ID, DOWNLOADED_RECITER_ID)
        } returns true
        everySuspend { quranRepository.getReciters() } returns dummyReciters

        testViewModel = DownloadedRecitersViewModel(
            quranRepository = quranRepository,
            surahArgs = surahArgs,
            dispatcher = testDispatcher,

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

        testViewModel = DownloadedRecitersViewModel(
            quranRepository = quranRepository,
            surahArgs = surahArgs,
            dispatcher = testDispatcher,
        )
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.onQueryChange(ABDUL_QUERY)

        val filteredReciter = testViewModel.uiState.value.reciters
            .find { it.id == DOWNLOADED_RECITER_ID }
        assertTrue(filteredReciter?.isDownloaded ?: false)
    }

    @Test
    fun `multiple filter operations should work correctly`() = runTest {
        testViewModel.onQueryChange(ABDUL_QUERY)
        val firstFilterSize = testViewModel.uiState.value.reciters.size

        testViewModel.onQueryChange(BASIT_QUERY)
        val secondFilterSize = testViewModel.uiState.value.reciters.size

        testViewModel.onClearQueryClick()
        val allRecitersSize = testViewModel.uiState.value.reciters.size

        assertTrue(secondFilterSize <= firstFilterSize)
        assertEquals(dummyReciters.size, allRecitersSize)
    }

    @Test
    fun `filter should be case insensitive for English names`() = runTest {
        testViewModel.onQueryChange(LOWERCASE_QUERY)
        val lowercaseResults = testViewModel.uiState.value.reciters.size

        testViewModel.onQueryChange(UPPERCASE_QUERY)
        val uppercaseResults = testViewModel.uiState.value.reciters.size

        assertEquals(lowercaseResults, uppercaseResults)
    }

    @Test
    fun `allReciters should remain unchanged after multiple operations`() = runTest {
        val initialAllReciters = testViewModel.uiState.value.reciters

        testViewModel.onQueryChange(FILTER_QUERY)
        testViewModel.onQueryChange(ANOTHER_QUERY)
        testViewModel.onClearQueryClick()
        testViewModel.onQueryChange(FILTER_QUERY)
        testViewModel.onClearQueryClick()

        assertEquals(initialAllReciters, testViewModel.uiState.value.reciters)
    }

    @Test
    fun `swipeable state should be passed from args`() = runTest {
        everySuspend { surahArgs.isSwipeToDeleteEnabled } returns false

        testViewModel = DownloadedRecitersViewModel(
            quranRepository = quranRepository,
            surahArgs = surahArgs,
            dispatcher = testDispatcher,
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, testViewModel.uiState.value.isSwipeable)
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
        const val NO_MATCH_QUERY = "XYZ123"
        const val ANOTHER_QUERY = "Mahmoud"
        const val LOWERCASE_QUERY = "abdul"
        const val UPPERCASE_QUERY = "ABDUL"
        const val EMPTY_STRING = ""
        const val BLANK_STRING = "   "

        val reciters = Reciter(
            id = 1,
            name = "Abdul Basit Abdul Samad",
            arabicName = "عبد الباسط عبد الصمد",
            tilawahType = "Murattal"
        )

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
            ),
            Reciter(
                id = 4,
                name = "Abdul Rahman Al-Sudais",
                arabicName = "عبد الرحمن السديس",
                tilawahType = "Murattal"
            )
        )
    }
}
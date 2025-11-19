package net.thechance.mena.faith.presentation.feature.quran.search

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.SearchEffect
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.SearchViewModel
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.args.SearchArgs
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testViewModel: SearchViewModel
    private val quranRepository: QuranRepository = mock(mode = MockMode.autofill)
    private val searchArgs: SearchArgs = mock(mode = MockMode.autofill)

    @BeforeTest
    fun setup() {
        startKoin {
            modules(module { single { mock<SnackbarHandler>(MockMode.autofill) } })
        }
        testDispatcher = StandardTestDispatcher()
        everySuspend { searchArgs.surahId } returns null
        everySuspend { searchArgs.surahName } returns null

        testViewModel = SearchViewModel(
            searchArgs = searchArgs,
            repository = quranRepository,
            dispatcher = testDispatcher
        )
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `onQueryChange should update query in state when called`() = runTest {
        testViewModel.onQueryChange(TEST_QUERY)

        assertEquals(TEST_QUERY, testViewModel.uiState.value.query)
    }

    @Test
    fun `onQueryChange should clear search results when query length is less than 2`() = runTest {
        everySuspend { quranRepository.searchForAyahInQuran(VALID_QUERY) } returns dummyAyat
        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceTimeBy(SEARCH_DELAY)
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.onQueryChange(SHORT_QUERY)

        assertTrue(testViewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `onQueryChange should cancel previous search when called multiple times`() = runTest {
        everySuspend { quranRepository.searchForAyahInQuran(FIRST_QUERY) } returns dummyAyat
        everySuspend { quranRepository.searchForAyahInQuran(SECOND_QUERY) } returns emptyList()
        testViewModel.onQueryChange(FIRST_QUERY)
        testDispatcher.scheduler.advanceTimeBy(HALF_SEARCH_DELAY)

        testViewModel.onQueryChange(SECOND_QUERY)
        testDispatcher.scheduler.advanceTimeBy(SEARCH_DELAY)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(testViewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `onQueryChange should return empty results when repository returns empty list`() = runTest {
        everySuspend { quranRepository.searchForAyahInQuran(VALID_QUERY) } returns emptyList()

        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceTimeBy(SEARCH_DELAY)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(testViewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `onQueryChange with single character should not trigger search`() = runTest {
        everySuspend { quranRepository.searchForAyahInQuran(SINGLE_CHAR_QUERY) } returns dummyAyat

        testViewModel.onQueryChange(SINGLE_CHAR_QUERY)
        testDispatcher.scheduler.advanceTimeBy(SEARCH_DELAY)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(testViewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `onQueryChange with empty string should clear results`() = runTest {
        everySuspend { quranRepository.searchForAyahInQuran(VALID_QUERY) } returns dummyAyat
        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceTimeBy(SEARCH_DELAY)
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.onQueryChange(EMPTY_STRING)

        assertTrue(testViewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `searchJob should be cancelled when new query is entered`() = runTest {
        everySuspend { quranRepository.searchForAyahInQuran(FIRST_QUERY) } returns dummyAyat
        everySuspend { quranRepository.searchForAyahInQuran(SECOND_QUERY) } returns emptyList()
        testViewModel.onQueryChange(FIRST_QUERY)
        testDispatcher.scheduler.advanceTimeBy(HALF_SEARCH_DELAY)

        testViewModel.onQueryChange(SECOND_QUERY)
        testDispatcher.scheduler.advanceTimeBy(SEARCH_DELAY)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(testViewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `onClearQueryClick should clear query in state when called`() = runTest {
        testViewModel.onQueryChange(TEST_QUERY)

        testViewModel.onClearQueryClick()

        assertEquals(EMPTY_STRING, testViewModel.uiState.value.query)
    }

    @Test
    fun `onClearQueryClick should not clear search results`() = runTest {
        everySuspend { quranRepository.searchForAyahInQuran(VALID_QUERY) } returns dummyAyat
        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceTimeBy(SEARCH_DELAY)
        testDispatcher.scheduler.advanceUntilIdle()
        val resultsBeforeClear = testViewModel.uiState.value.searchResults

        testViewModel.onClearQueryClick()

        assertEquals(resultsBeforeClear, testViewModel.uiState.value.searchResults)
    }

    @Test
    fun `search results should persist after clearing query without new search`() = runTest {
        everySuspend { quranRepository.searchForAyahInQuran(VALID_QUERY) } returns dummyAyat
        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceTimeBy(SEARCH_DELAY)
        testDispatcher.scheduler.advanceUntilIdle()
        val resultsCount = testViewModel.uiState.value.searchResults.size

        testViewModel.onClearQueryClick()

        assertEquals(resultsCount, testViewModel.uiState.value.searchResults.size)
    }

    @Test
    fun `onBackClick should navigate back when called`() = runTest {
        testViewModel.uiEffect.test {
            testViewModel.onBackClick()

            val effect = awaitItem()
            assertTrue(effect is SearchEffect.NavigateBack)
            assertEquals(null, effect.ayahNumber)
        }
    }

    @Test
    fun `onSearchResultClick should navigate back with ayahId when surahId is not null`() =
        runTest {
            everySuspend { searchArgs.surahId } returns TEST_SURAH_ID
            everySuspend { searchArgs.surahName } returns TEST_SURAH_NAME
            testViewModel = SearchViewModel(
                searchArgs = searchArgs,
                repository = quranRepository,
                dispatcher = testDispatcher
            )
            testDispatcher.scheduler.advanceUntilIdle()

            testViewModel.uiEffect.test {
                testViewModel.onSearchResultClick(TEST_SURAH_ID, TEST_AYAH_ID)

                val effect = awaitItem()
                assertTrue(effect is SearchEffect.NavigateBack)
                assertEquals(TEST_AYAH_ID, effect.ayahNumber)
            }
        }

    @Test
    fun `onSearchResultClick should navigate to surah when surahId is null`() = runTest {
        testViewModel.uiEffect.test {
            testViewModel.onSearchResultClick(SURAH_ID_FOR_NAVIGATION, TEST_AYAH_ID)

            val effect = awaitItem()
            assertTrue(effect is SearchEffect.NavigateToSurah)
            assertEquals(SURAH_ID_FOR_NAVIGATION, effect.surahId)
            assertEquals(TEST_AYAH_ID, effect.ayahId)
        }
    }

    @Test
    fun `onSearchResultClick should use correct surah name from enum`() = runTest {
        testViewModel.uiEffect.test {
            testViewModel.onSearchResultClick(1, TEST_AYAH_ID)

            val effect = awaitItem() as SearchEffect.NavigateToSurah
            assertEquals(effect.surahName, TEST_FIRST_SURAH)
        }
    }

    @Test
    fun `state should initialize with correct surahId from args`() = runTest {
        everySuspend { searchArgs.surahId } returns TEST_SURAH_ID
        testViewModel = SearchViewModel(
            searchArgs = searchArgs,
            repository = quranRepository,
            dispatcher = testDispatcher
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TEST_SURAH_ID, testViewModel.uiState.value.surahId)
    }

    @Test
    fun `state should initialize with empty query`() = runTest {
        assertEquals(EMPTY_STRING, testViewModel.uiState.value.query)
    }

    @Test
    fun `state should initialize with empty search results`() = runTest {
        assertTrue(testViewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `test searchForAyahInSurah is called when surahId is present`() = runTest {
        every { searchArgs.surahId } returns TEST_SURAH_ID
        every { searchArgs.surahName } returns TEST_SURAH_NAME
        everySuspend {
            quranRepository.searchForAyahInSurah(
                TEST_SURAH_ID,
                TEST_QUERY
            )
        } returns dummyAyat
        testViewModel = SearchViewModel(searchArgs, quranRepository, testDispatcher)

        testViewModel.onQueryChange(TEST_QUERY)
        testDispatcher.scheduler.advanceTimeBy(SEARCH_DELAY)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(exactly(1)) {
            quranRepository.searchForAyahInSurah(
                TEST_SURAH_ID,
                TEST_QUERY
            )
        }
        verifySuspend(exactly(0)) { quranRepository.searchForAyahInQuran(any()) }
        assertEquals(dummyAyat.size, testViewModel.uiState.value.searchResults.size)
    }

    private companion object {
        const val TEST_QUERY = "test query"
        const val VALID_QUERY = "الله"
        const val SHORT_QUERY = "ا"
        const val FIRST_QUERY = "first"
        const val SECOND_QUERY = "second"
        const val TEST_SURAH_ID = 1
        const val TEST_SURAH_NAME = "Al-Fatiha"
        const val TEST_FIRST_SURAH = "AlFatihah"
        const val EMPTY_STRING = ""
        const val SINGLE_CHAR_QUERY = "ا"
        const val SEARCH_DELAY = 1000L
        const val HALF_SEARCH_DELAY = 500L
        const val TEST_AYAH_ID = 5
        const val SURAH_ID_FOR_NAVIGATION = 2

        private val dummyAyat = listOf(
            Ayah(
                number = 1,
                surahId = 1,
                content = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                plainContent = "بسم الله الرحمن الرحيم"
            ),
            Ayah(
                number = 2,
                surahId = 1,
                content = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                plainContent = "الحمد لله العالمين"
            ),
            Ayah(
                number = 3,
                surahId = 1,
                content = "الرَّحْمَٰنِ الرَّحِيمِ",
                plainContent = "الرحمن الرحيم"
            )
        )
    }
}
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
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.quran
import mena.faith_presentation.generated.resources.search_in_surah_hint
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.feature.quran.search.args.ISearchArgs
import net.thechance.mena.faith.presentation.util.provider.StringResourceProvider
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testViewModel: SearchViewModel
    private val quranRepository: QuranRepository = mock(mode = MockMode.autofill)
    private val searchArgs: ISearchArgs = mock(mode = MockMode.autofill)
    private lateinit var stringResourceProvider: StringResourceProvider

    @BeforeTest
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        stringResourceProvider = mock(mode = MockMode.autofill)

        everySuspend { stringResourceProvider.getString(Res.string.quran) } returns QURAN_TEXT
        everySuspend {
            stringResourceProvider.getString(Res.string.search_in_surah_hint, any())
        } returns SEARCH_HINT

        everySuspend { searchArgs.surahId } returns null
        everySuspend { searchArgs.surahName } returns null

        testViewModel = SearchViewModel(
            searchArgs = searchArgs,
            repository = quranRepository,
            dispatcher = testDispatcher,
            stringResourceProvider = stringResourceProvider
        )
        testDispatcher.scheduler.advanceUntilIdle()
    }

    // ============ Query Change Tests ============

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

        assertTrue(testViewModel.uiState.value.searchResult.isEmpty())
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

        assertTrue(testViewModel.uiState.value.searchResult.isEmpty())
    }

    @Test
    fun `onQueryChange should return empty results when repository returns empty list`() = runTest {
        everySuspend { quranRepository.searchForAyahInQuran(VALID_QUERY) } returns emptyList()

        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceTimeBy(SEARCH_DELAY)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(testViewModel.uiState.value.searchResult.isEmpty())
    }

    @Test
    fun `onQueryChange with single character should not trigger search`() = runTest {
        everySuspend { quranRepository.searchForAyahInQuran(SINGLE_CHAR_QUERY) } returns dummyAyat

        testViewModel.onQueryChange(SINGLE_CHAR_QUERY)
        testDispatcher.scheduler.advanceTimeBy(SEARCH_DELAY)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(testViewModel.uiState.value.searchResult.isEmpty())
    }

    @Test
    fun `onQueryChange with empty string should clear results`() = runTest {
        everySuspend { quranRepository.searchForAyahInQuran(VALID_QUERY) } returns dummyAyat
        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceTimeBy(SEARCH_DELAY)
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.onQueryChange(EMPTY_STRING)

        assertTrue(testViewModel.uiState.value.searchResult.isEmpty())
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

        assertTrue(testViewModel.uiState.value.searchResult.isEmpty())
    }

    // ============ Clear Query Tests ============

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

        val resultsBeforeClear = testViewModel.uiState.value.searchResult

        testViewModel.onClearQueryClick()

        assertEquals(resultsBeforeClear, testViewModel.uiState.value.searchResult)
    }

    @Test
    fun `search results should persist after clearing query without new search`() = runTest {
        everySuspend { quranRepository.searchForAyahInQuran(VALID_QUERY) } returns dummyAyat

        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceTimeBy(SEARCH_DELAY)
        testDispatcher.scheduler.advanceUntilIdle()

        val resultsCount = testViewModel.uiState.value.searchResult.size

        testViewModel.onClearQueryClick()

        assertEquals(resultsCount, testViewModel.uiState.value.searchResult.size)
    }

    // ============ Navigation Tests ============

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
            everySuspend {
                stringResourceProvider.getString(Res.string.search_in_surah_hint, TEST_SURAH_NAME)
            } returns "Search in $TEST_SURAH_NAME"

            testViewModel = SearchViewModel(
                searchArgs = searchArgs,
                repository = quranRepository,
                dispatcher = testDispatcher,
                stringResourceProvider = stringResourceProvider
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

    // ============ State Initialization Tests ============

    @Test
    fun `state should initialize with correct surahId and surahName from args`() = runTest {
        everySuspend { searchArgs.surahId } returns TEST_SURAH_ID
        everySuspend { searchArgs.surahName } returns TEST_SURAH_NAME

        testViewModel = SearchViewModel(
            searchArgs = searchArgs,
            repository = quranRepository,
            dispatcher = testDispatcher,
            stringResourceProvider = stringResourceProvider
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TEST_SURAH_ID, testViewModel.uiState.value.surahId)
        assertEquals(TEST_SURAH_NAME, testViewModel.uiState.value.surahName)
    }

    @Test
    fun `state should initialize with empty query`() = runTest {
        assertEquals(EMPTY_STRING, testViewModel.uiState.value.query)
    }

    @Test
    fun `state should initialize with empty search results`() = runTest {
        assertTrue(testViewModel.uiState.value.searchResult.isEmpty())
    }

    @Test
    fun `init should set hint with surah name when surahName is not null`() = runTest {
        val expectedHint = TEST_SEARCH_BY_SURAH_NAME
        everySuspend { searchArgs.surahId } returns TEST_SURAH_ID
        everySuspend { searchArgs.surahName } returns TEST_SURAH_NAME
        everySuspend {
            stringResourceProvider.getString(Res.string.search_in_surah_hint, TEST_SURAH_NAME)
        } returns expectedHint

        testViewModel = SearchViewModel(
            searchArgs = searchArgs,
            repository = quranRepository,
            dispatcher = testDispatcher,
            stringResourceProvider = stringResourceProvider
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(expectedHint, testViewModel.uiState.value.hint)
    }

    @Test
    fun `test searchForAyahInSurah is called when surahId is present`() = runTest {
        // Given
        every { searchArgs.surahId } returns TEST_SURAH_ID
        every { searchArgs.surahName } returns TEST_SURAH_NAME
        everySuspend {
            quranRepository.searchForAyahInSurah(
                TEST_SURAH_ID,
                TEST_QUERY
            )
        } returns dummyAyat

        testViewModel =
            SearchViewModel(searchArgs, quranRepository, testDispatcher, stringResourceProvider)

        // When
        testViewModel.onQueryChange(TEST_QUERY)
        testDispatcher.scheduler.advanceTimeBy(SEARCH_DELAY)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verifySuspend(exactly(1)) {
            quranRepository.searchForAyahInSurah(
                TEST_SURAH_ID,
                TEST_QUERY
            )
        }
        verifySuspend(exactly(0)) { quranRepository.searchForAyahInQuran(any()) }
        assertEquals(dummyAyat.size, testViewModel.uiState.value.searchResult.size)
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
        const val TEST_SEARCH_BY_SURAH_NAME = "Search in Al-Fatiha"
        const val EMPTY_STRING = ""
        const val QURAN_TEXT = "Quran"
        const val SEARCH_HINT = "Search in..."
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
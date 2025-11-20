package net.thechance.mena.faith.presentation.feature.quran.reciter.reciterSelection

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
class ReciterSelectionViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testViewModel: ReciterSelectionViewModel
    private val quranRepository: QuranRepository = mock(mode = MockMode.autofill)
    private val downloadedRecitersArgs: DownloadedRecitersArgs = mock(mode = MockMode.autofill)

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
        everySuspend { downloadedRecitersArgs.surahId } returns TEST_SURAH_ID
        everySuspend { quranRepository.getReciters() } returns dummyReciters
        everySuspend { quranRepository.isSurahAudioCached(TEST_SURAH_ID, any()) } returns false

        testViewModel = ReciterSelectionViewModel(
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
    fun `init should fetch all reciters`() = runTest {
        verifySuspend(atLeast(1)) { quranRepository.getReciters() }
        assertEquals(dummyReciters.size, testViewModel.uiState.value.searchResults.size)
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
            assertTrue(effect is ReciterSelectionEffect.NavigateBack)
        }
    }

    @Test
    fun `onQueryChange should update query in state`() = runTest {
        testViewModel.onQueryChange(TEST_QUERY)

        assertEquals(TEST_QUERY, testViewModel.uiState.value.query)
    }

    @Test
    fun `onQueryChange with valid query should trigger search`() = runTest {
        everySuspend { quranRepository.searchForReciter(VALID_QUERY) } returns filteredReciters

        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(exactly(1)) { quranRepository.searchForReciter(VALID_QUERY) }
        assertEquals(filteredReciters.size, testViewModel.uiState.value.searchResults.size)
    }

    @Test
    fun `onQueryChange with single character should fetch all reciters`() = runTest {
        everySuspend { quranRepository.getReciters() } returns dummyReciters

        testViewModel.onQueryChange(SINGLE_CHAR_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(atLeast(1)) { quranRepository.getReciters() }
    }

    @Test
    fun `onQueryChange with empty string should fetch all reciters`() = runTest {
        everySuspend { quranRepository.searchForReciter(VALID_QUERY) } returns filteredReciters
        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.onQueryChange(EMPTY_STRING)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(atLeast(2)) { quranRepository.getReciters() }
    }

    @Test
    fun `onQueryChange should cancel previous search`() = runTest {
        everySuspend { quranRepository.searchForReciter(FIRST_QUERY) } returns filteredReciters
        everySuspend { quranRepository.searchForReciter(SECOND_QUERY) } returns emptyList()

        testViewModel.onQueryChange(FIRST_QUERY)
        testViewModel.onQueryChange(SECOND_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(exactly(0)) { quranRepository.searchForReciter(FIRST_QUERY) }
        verifySuspend(exactly(1)) { quranRepository.searchForReciter(SECOND_QUERY) }
    }

    @Test
    fun `onQueryChange with same query should not trigger new search`() = runTest {
        everySuspend { quranRepository.searchForReciter(VALID_QUERY) } returns filteredReciters

        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(exactly(1)) { quranRepository.searchForReciter(VALID_QUERY) }
    }

    @Test
    fun `onClearQueryClick should clear query`() = runTest {
        testViewModel.onQueryChange(TEST_QUERY)

        testViewModel.onClearQueryClick()

        assertEquals(EMPTY_STRING, testViewModel.uiState.value.query)
    }

    @Test
    fun `onClearQueryClick should fetch all reciters`() = runTest {
        everySuspend { quranRepository.searchForReciter(VALID_QUERY) } returns filteredReciters
        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.onClearQueryClick()
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(atLeast(2)) { quranRepository.getReciters() }
    }

    @Test
    fun `search should return empty results when repository returns empty list`() = runTest {
        everySuspend { quranRepository.searchForReciter(VALID_QUERY) } returns emptyList()

        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(testViewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `query with two characters should trigger search`() = runTest {
        everySuspend { quranRepository.searchForReciter(TWO_CHAR_QUERY) } returns filteredReciters

        testViewModel.onQueryChange(TWO_CHAR_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(exactly(1)) { quranRepository.searchForReciter(TWO_CHAR_QUERY) }
    }

    @Test
    fun `multiple rapid query changes should only execute last search`() = runTest {
        everySuspend { quranRepository.searchForReciter(FIRST_QUERY) } returns filteredReciters
        everySuspend { quranRepository.searchForReciter(SECOND_QUERY) } returns filteredReciters
        everySuspend { quranRepository.searchForReciter(THIRD_QUERY) } returns filteredReciters

        testViewModel.onQueryChange(FIRST_QUERY)
        testViewModel.onQueryChange(SECOND_QUERY)
        testViewModel.onQueryChange(THIRD_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(exactly(0)) { quranRepository.searchForReciter(FIRST_QUERY) }
        verifySuspend(exactly(0)) { quranRepository.searchForReciter(SECOND_QUERY) }
        verifySuspend(exactly(1)) { quranRepository.searchForReciter(THIRD_QUERY) }
    }

    @Test
    fun `search with Arabic text should work correctly`() = runTest {
        everySuspend { quranRepository.searchForReciter(ARABIC_QUERY) } returns filteredReciters

        testViewModel.onQueryChange(ARABIC_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(exactly(1)) { quranRepository.searchForReciter(ARABIC_QUERY) }
        assertEquals(filteredReciters.size, testViewModel.uiState.value.searchResults.size)
    }

    @Test
    fun `fetching all reciters after search should replace search results`() = runTest {
        everySuspend { quranRepository.searchForReciter(VALID_QUERY) } returns filteredReciters
        testViewModel.onQueryChange(VALID_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.onClearQueryClick()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(dummyReciters.size, testViewModel.uiState.value.searchResults.size)
    }

    private companion object {
        const val TEST_SURAH_ID = 1
        const val TEST_QUERY = "Abdul"
        const val VALID_QUERY = "Basit"
        const val FIRST_QUERY = "Ab"
        const val SECOND_QUERY = "Abd"
        const val THIRD_QUERY = "Abdu"
        const val SINGLE_CHAR_QUERY = "A"
        const val TWO_CHAR_QUERY = "Ab"
        const val EMPTY_STRING = ""
        const val ARABIC_QUERY = "عبد"

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

        private val filteredReciters = listOf(
            Reciter(
                id = 1,
                name = "Abdul Basit Abdul Samad",
                arabicName = "عبد الباسط عبد الصمد",
                tilawahType = "Murattal"
            )
        )
    }
}
package net.thechance.mena.faith.presentation.feature.quran.reciter.reciterSelection

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.domain.usecase.SearchRecitersUseCase
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
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
    private lateinit var searchRecitersUseCase: SearchRecitersUseCase

    @BeforeTest
    fun setup() {
        startKoin {
            modules(module { single { mock<SnackbarHandler>(MockMode.autofill) } })
        }

        testDispatcher = StandardTestDispatcher()
        searchRecitersUseCase = SearchRecitersUseCase()

        everySuspend { quranRepository.getReciters() } returns dummyReciters
        everySuspend { quranRepository.getDefaultReciter() } returns flowOf(DEFAULT_RECITER_ID)

        testViewModel = ReciterSelectionViewModel(
            repository = quranRepository,
            searchRecitersUseCase = searchRecitersUseCase,
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
    fun `onQueryChange should filter reciters correctly`() = runTest {
        testViewModel.onQueryChange(FILTER_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()
        val expected = dummyReciters.filter { it.name.contains(FILTER_QUERY, ignoreCase = true) }
        assertEquals(expected.size, testViewModel.uiState.value.searchResults.size)
    }

    @Test
    fun `onQueryChange with empty string should restore all reciters`() = runTest {
        testViewModel.onQueryChange(FILTER_QUERY)
        testDispatcher.scheduler.advanceUntilIdle()
        testViewModel.onClearQueryClick()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(dummyReciters.size, testViewModel.uiState.value.searchResults.size)
    }

    @Test
    fun `onClearQueryClick should clear query`() = runTest {
        testViewModel.onQueryChange(TEST_QUERY)
        testViewModel.onClearQueryClick()
        assertEquals(EMPTY_STRING, testViewModel.uiState.value.query)
    }

    @Test
    fun `onSelectReciterClick should update selected reciter in state`() = runTest {
        everySuspend { quranRepository.saveDefaultReciter(TEST_RECITER_ID) } returns Unit
        testViewModel.onSelectReciterClick(TEST_RECITER_ID)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(TEST_RECITER_ID, testViewModel.uiState.value.selectedReciterId)
    }


    private companion object {
        const val DEFAULT_RECITER_ID = 1
        const val TEST_RECITER_ID = 1
        const val TEST_QUERY = "Abdul"
        const val FILTER_QUERY = "Abdul"
        const val EMPTY_STRING = ""

        private val dummyReciters = listOf(
            Reciter(1, "Abdul Basit Abdul Samad", "عبد الباسط عبد الصمد", "Murattal"),
            Reciter(2, "Mahmoud Khalil Al-Hussary", "محمود خليل الحصري", "Murattal"),
            Reciter(3, "Mishary Rashid Alafasy", "مشاري بن راشد العفاسي", "Murattal"),
            Reciter(4, "Abdul Rahman Al-Sudais", "عبد الرحمن السديس", "Murattal")
        )
    }
}
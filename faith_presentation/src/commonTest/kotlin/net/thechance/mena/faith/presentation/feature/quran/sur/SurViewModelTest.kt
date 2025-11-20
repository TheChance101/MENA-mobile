package net.thechance.mena.faith.presentation.feature.quran.sur

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_al_baqarah
import mena.faith_presentation.generated.resources.ic_al_fatihah
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SurViewModelTest {
    private lateinit var quranRepository: QuranRepository
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: SurViewModel

    @BeforeTest
    fun setUp() {
        startKoin {
            modules(module { single { mock<SnackbarHandler>(MockMode.autofill) } })
        }
        quranRepository = mock(MockMode.autofill)
        testDispatcher = StandardTestDispatcher()
        viewModel = SurViewModel(quranRepository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `initialize view model should set empty state when getSur returns no data`() = runTest {
        everySuspend { quranRepository.getSur() } returns emptyList()

        viewModel = SurViewModel(quranRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(emptyUiState, viewModel.uiState.value)
    }

    @Test
    fun `initialize view model should set success state when getSur returns data`() =
        runTest {
            everySuspend { quranRepository.getSur() } returns surList

            viewModel = SurViewModel(quranRepository, testDispatcher)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                emptyUiState.copy(sur = surahUiList),
                viewModel.uiState.value
            )
        }

    @Test
    fun `onSurahClick should navigate to surah details screen with surah id and name`() = runTest {
        everySuspend { quranRepository.getSur() } returns emptyList()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onSurahClick(AL_FATIHAH_ID)

            assertEquals(
                SurEffect.NavigateToSurahDetails(AL_FATIHAH_ID),
                awaitItem()
            )
        }
    }

    @Test
    fun `onBackClick should navigate back`() = runTest {
        everySuspend { quranRepository.getSur() } returns emptyList()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onBackClick()

            assertEquals(
                SurEffect.NavigateBack,
                awaitItem()
            )
        }
    }

    @Test
    fun `onBookmarkClick should navigate to bookmark screen`() = runTest {
        everySuspend { quranRepository.getSur() } returns emptyList()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onBookmarkClick()

            assertEquals(
                SurEffect.NavigateToBookmark,
                awaitItem()
            )
        }
    }

    private companion object {
        val emptyUiState = SurUiState(emptyList())
        const val AL_FATIHAH_ID = 1
        val surList = listOf(
            Surah(
                id = 1,
                order = Surah.SurahOrder.AlFatihah,
                name = "Al-Fatihah",
                ayahCount = 7,
            ),
            Surah(
                id = 2,
                order = Surah.SurahOrder.AlBaqarah,
                name = "Al-Baqarah",
                ayahCount = 286,
            )
        )

        val surahUiList = listOf(
            SurUiState.SurahUiState(
                id = 1,
                surahOrder = 1,
                arabicNameImg = Res.drawable.ic_al_fatihah,
                surahName = "Al-Fatihah",
                ayatCount = 7,
                isMakki = true
            ),
            SurUiState.SurahUiState(
                id = 2,
                surahOrder = 2,
                arabicNameImg = Res.drawable.ic_al_baqarah,
                surahName = "Al-Baqarah",
                ayatCount = 286,
                isMakki = false
            )
        )
    }
}

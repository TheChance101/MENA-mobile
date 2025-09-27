package net.thechance.mena.faith.presentation.feature.quran.sur

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_al_baqarah
import mena.faith_presentation.generated.resources.ic_al_fatihah
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.QuranRepository
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SurViewModelTest {

    private val quranRepository: QuranRepository = mock(MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private var viewModel = SurViewModel(quranRepository, testDispatcher)

    @Test
    fun `initialize view model should set empty state when getAllSur returns no data`() = runTest {
        // Given
        everySuspend { quranRepository.getAllSur() } returns emptyList()

        // When
        viewModel = SurViewModel(quranRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(emptyUiState, viewModel.uiState.value)
    }

    @Test
    fun `initialize view model should set success state when getAllSur returns data`() =
        runTest {
            // Given
            everySuspend { quranRepository.getAllSur() } returns surList

            // When
            viewModel = SurViewModel(quranRepository, testDispatcher)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            assertEquals(
                emptyUiState.copy(sur = surahUiList, errorMessage = null, isLoading = false),
                viewModel.uiState.value
            )
        }

    @Test
    fun `initialize view model should handle error state when getAllSur fails`() = runTest {
        // Given
        everySuspend { quranRepository.getAllSur() } throws Exception("Error")

        // When
        viewModel = SurViewModel(quranRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(
            emptyUiState.copy(errorMessage = "Error", isLoading = false),
            viewModel.uiState.value
        )
    }

    @Test
    fun `onSurahClick should navigate to surah details screen with surah id and name`() = runTest {
        // Given
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.uiEffect.test {
            viewModel.onSurahClick(AL_FATIHAH_ID, AL_FATIHAH_NAME)

            // Then
            assertEquals(
                SurEffect.NavigateToSurahDetails(AL_FATIHAH_ID, AL_FATIHAH_NAME),
                awaitItem()
            )
        }
    }

    @Test
    fun `onBackClick should navigate back`() = runTest {
        // Given
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.uiEffect.test {
            viewModel.onBackClick()

            // Then
            assertEquals(
                SurEffect.NavigateBack,
                awaitItem()
            )
        }
    }

    @Test
    fun `onBookmarkClick should navigate to bookmark screen`() = runTest {
        // Given
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.uiEffect.test {
            viewModel.onBookmarkClick()

            // Then
            assertEquals(
                SurEffect.NavigateToBookmark,
                awaitItem()
            )
        }
    }

    private companion object {
        val emptyUiState = SurScreenState(
            isLoading = false,
            errorMessage = null,
            sur = emptyList()
        )
        const val AL_FATIHAH_ID = 1
        const val AL_FATIHAH_NAME = "Al-Fatihah"
        val surList = listOf(
            Surah(
                id = 1,
                order = Surah.SurahOrder.AlFatihah,
                name = "Al-Fatihah",
                ayahCount = 7,
                isMakkia = true
            ),
            Surah(
                id = 2,
                order = Surah.SurahOrder.AlBaqarah,
                name = "Al-Baqarah",
                ayahCount = 286,
                isMakkia = false
            )
        )

        val surahUiList = listOf(
            SurScreenState.SurahUiState(
                id = 1,
                surahOrder = 1,
                arabicNameImg = Res.drawable.ic_al_fatihah,
                surahName = "Al-Fatihah",
                ayatCount = 7,
                isMakki = true
            ),
            SurScreenState.SurahUiState(
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

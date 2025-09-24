package net.thechance.mena.faith.presentation.feature.quran.surah

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.util.ClipboardManager
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SurahViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val quranRepository: QuranRepository = mock(mode = MockMode.autofill)
    private val clipboardManager: ClipboardManager = mock(mode = MockMode.autofill)

    private val viewModel = SurahViewModel(
        surahId = DEFAULT_SURAH_ID,
        surahName = DEFAULT_SURAH_NAME,
        dispatcher = testDispatcher,
        quranRepository = quranRepository,
        clipboardManager = clipboardManager
    )

    @BeforeTest
    fun setup() {
        everySuspend { quranRepository.getAyatOfSurah(any()) } returns emptyList()
    }

    @Test
    fun `SurahViewModel should return correct surah id when viewModel is created`() = runTest {
        // When
        val testViewModel = SurahViewModel(
            surahId = TEST_SURAH_ID,
            surahName = DEFAULT_SURAH_NAME,
            dispatcher = testDispatcher,
            quranRepository = quranRepository,
            clipboardManager = clipboardManager
        )

        // Then
        assertEquals(TEST_SURAH_ID, testViewModel.uiState.value.surahId)
    }

    @Test
    fun `SurahViewModel should return correct surah name when viewModel is created`() = runTest {
        // When
        val testViewModel = SurahViewModel(
            surahId = DEFAULT_SURAH_ID,
            surahName = TEST_SURAH_NAME,
            dispatcher = testDispatcher,
            quranRepository = quranRepository,
            clipboardManager = clipboardManager
        )

        // Then
        assertEquals(TEST_SURAH_NAME, testViewModel.uiState.value.surahName)
    }

    @Test
    fun `loadSurahData should handle exception gracefully when repository throws exception`() = runTest {
        // Given
        everySuspend { quranRepository.getAyatOfSurah(any()) } throws Exception(NETWORK_ERROR_MESSAGE)

        // When
        val testViewModel = SurahViewModel(
            surahId = DEFAULT_SURAH_ID,
            surahName = DEFAULT_SURAH_NAME,
            dispatcher = testDispatcher,
            quranRepository = quranRepository,
            clipboardManager = clipboardManager
        )

        // Then
        assertTrue(testViewModel.uiState.value.ayatOfSurah.isEmpty())
    }

    @Test
    fun `onBackClick should emit NavigateBack effect when called`() = runTest {
        // When & Then
        viewModel.uiEffect.test {
            viewModel.onBackClick()
            val effect = awaitItem()
            assertEquals(SurahScreenEffect.NavigateBack, effect)
        }
    }

    @Test
    fun `onAyahLongPress should return true for isAyahActionButtonsVisible when called`() = runTest {
        // When
        viewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // Then
        assertTrue(viewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onAyahLongPress should return ayah content for selectedAyah when called`() = runTest {
        // When
        viewModel.onAyahLongPress(SELECTED_AYAH_CONTENT, TEST_AYAH_INDEX)

        // Then
        assertEquals(SELECTED_AYAH_CONTENT, viewModel.uiState.value.selectedAyah)
    }

    @Test
    fun `onAyahLongPress should return ayah index for selectedAyahIndex when called`() = runTest {
        // When
        viewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // Then
        assertEquals(TEST_AYAH_INDEX, viewModel.uiState.value.selectedAyahIndex)
    }

    @Test
    fun `onAyahLongPress should return new content when different ayah is selected`() = runTest {
        // Given
        viewModel.onAyahLongPress(FIRST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // When
        viewModel.onAyahLongPress(SECOND_AYAH_CONTENT, SECOND_AYAH_INDEX)

        // Then
        assertEquals(SECOND_AYAH_CONTENT, viewModel.uiState.value.selectedAyah)
        assertEquals(SECOND_AYAH_INDEX, viewModel.uiState.value.selectedAyahIndex)
    }

    @Test
    fun `onAyahLongPress should return negative value when called with negative index`() = runTest {
        // When
        viewModel.onAyahLongPress(TEST_AYAH_CONTENT, NEGATIVE_AYAH_INDEX)

        // Then
        assertEquals(NEGATIVE_AYAH_INDEX, viewModel.uiState.value.selectedAyahIndex)
    }

    @Test
    fun `onDismissActionButtons should return false for isAyahActionButtonsVisible when called`() =
        runTest {
            // Given
            viewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

            // When
            viewModel.onDismissActionButtons()

            // Then
            assertFalse(viewModel.uiState.value.isAyahActionButtonsVisible)
        }

    @Test
    fun `onDismissActionButtons should return empty string for selectedAyah when called`() =
        runTest {
            // Given
            viewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

            // When
            viewModel.onDismissActionButtons()

            // Then
            assertEquals(EMPTY_STRING, viewModel.uiState.value.selectedAyah)
        }

    @Test
    fun `onDismissActionButtons should return null for selectedAyahIndex when called`() = runTest {
        // Given
        viewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // When
        viewModel.onDismissActionButtons()

        // Then
        assertNull(viewModel.uiState.value.selectedAyahIndex)
    }

    @Test
    fun `onDismissActionButtons should return false when ayah was long pressed`() = runTest {
        // Given
        viewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // When
        viewModel.onDismissActionButtons()

        // Then
        assertFalse(viewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onDismissActionButtons should return empty string when ayah was long pressed`() = runTest {
        // Given
        viewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // When
        viewModel.onDismissActionButtons()

        // Then
        assertEquals(EMPTY_STRING, viewModel.uiState.value.selectedAyah)
    }

    @Test
    fun `onBookmarkClick should return false for isAyahActionButtonsVisible when called`() = runTest {
        // Given
        viewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // When
        viewModel.onBookmarkClick(TEST_AYAH_NUMBER)

        // Then
        assertFalse(viewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onBookmarkClick should return null for selectedAyahIndex when called`() = runTest {
        // Given
        viewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // When
        viewModel.onBookmarkClick(TEST_AYAH_NUMBER)

        // Then
        assertNull(viewModel.uiState.value.selectedAyahIndex)
    }

    @Test
    fun `onBookmarkClick should return false for isAyahActionButtonsVisible when called with zero ayah number`() = runTest {
        // Given
        viewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // When
        viewModel.onBookmarkClick(ZERO_AYAH_NUMBER)

        // Then
        assertFalse(viewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onShareClick should return false for isAyahActionButtonsVisible when called`() = runTest {
        // Given
        viewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // When
        viewModel.onShareClick(TEST_AYAH_CONTENT)

        // Then
        assertFalse(viewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onShareClick should return null for selectedAyahIndex when called`() = runTest {
        // Given
        viewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // When
        viewModel.onShareClick(TEST_AYAH_CONTENT)

        // Then
        assertNull(viewModel.uiState.value.selectedAyahIndex)
    }

    @Test
    fun `onShareClick should return ayah content for selectedAyah when called`() = runTest {
        // When
        viewModel.onShareClick(AYAH_TO_SHARE)

        // Then
        assertEquals(AYAH_TO_SHARE, viewModel.uiState.value.selectedAyah)
    }

    @Test
    fun `onShareClick should emit ShareAyah effect when called`() = runTest {
        // When & Then
        viewModel.uiEffect.test {
            viewModel.onShareClick(AYAH_TO_SHARE)
            val effect = awaitItem()
            assertEquals(SurahScreenEffect.ShareAyah(AYAH_TO_SHARE), effect)
        }
    }

    @Test
    fun `onShareClick should emit ShareAyah effect with empty content when called with empty string`() = runTest {
        // When & Then
        viewModel.uiEffect.test {
            viewModel.onShareClick(EMPTY_STRING)
            val effect = awaitItem()
            assertEquals(SurahScreenEffect.ShareAyah(EMPTY_STRING), effect)
        }
    }

    companion object {
        const val DEFAULT_SURAH_ID = 1
        const val DEFAULT_SURAH_NAME = "Al-Fatiha"
        const val TEST_SURAH_ID = 2
        const val TEST_SURAH_NAME = "Al-Baqarah"
        const val TEST_AYAH_INDEX = 0
        const val SECOND_AYAH_INDEX = 1
        const val NEGATIVE_AYAH_INDEX = -1
        const val ZERO_AYAH_NUMBER = 0
        const val TEST_AYAH_NUMBER = 1
        const val TEST_AYAH_CONTENT = "Test ayah"
        const val FIRST_AYAH_CONTENT = "First ayah"
        const val SECOND_AYAH_CONTENT = "Second ayah"
        const val SELECTED_AYAH_CONTENT = "Selected ayah content"
        const val AYAH_TO_SHARE = "Ayah to share"
        const val EMPTY_STRING = ""
        const val NETWORK_ERROR_MESSAGE = "Network error"
    }
}
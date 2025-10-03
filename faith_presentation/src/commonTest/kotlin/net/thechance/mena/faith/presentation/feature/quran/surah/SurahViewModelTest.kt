package net.thechance.mena.faith.presentation.feature.quran.surah

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.SnackBarState
import net.thechance.mena.faith.presentation.util.ClipboardManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SurahViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val quranRepository: QuranRepository = mock(mode = MockMode.autofill)
    private val bookmarkRepository: BookmarkRepository = mock(mode = MockMode.autofill)
    private val clipboardManager: ClipboardManager = mock(mode = MockMode.autofill)

    private fun createTestViewModel(
        surahId: Int = DEFAULT_SURAH_ID,
        surahName: String = DEFAULT_SURAH_NAME
    ): SurahViewModel {
        return SurahViewModel(
            surahId = surahId,
            surahName = surahName,
            dispatcher = testDispatcher,
            quranRepository = quranRepository,
            clipboardManager = clipboardManager,
            bookmarkRepository = bookmarkRepository
        )
    }

    @Test
    fun `onBackClick should navigate back when it called`() = runTest {
        // Given
        val testViewModel = createTestViewModel()

        // When & Then
        testViewModel.uiEffect.test {
            testViewModel.onBackClick()
            val effect = awaitItem()
            assertEquals(SurahScreenEffect.NavigateBack, effect)
        }
    }

    @Test
    fun `onAyahLongPress should show action buttons when it called`() = runTest {
        // Given
        val testViewModel = createTestViewModel()

        // When
        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // Then
        assertTrue(testViewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onAyahLongPress should return ayah content for selectedAyah when called`() = runTest {
        // Given
        val testViewModel = createTestViewModel()

        // When
        testViewModel.onAyahLongPress(SELECTED_AYAH_CONTENT, TEST_AYAH_INDEX)

        // Then
        assertEquals(SELECTED_AYAH_CONTENT, testViewModel.uiState.value.selectedAyah)
    }

    @Test
    fun `onAyahLongPress should return ayah index for selectedAyahIndex when called`() = runTest {
        // Given
        val testViewModel = createTestViewModel()

        // When
        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // Then
        assertEquals(TEST_AYAH_INDEX, testViewModel.uiState.value.selectedAyahIndex)
    }

    @Test
    fun `onAyahLongPress should return new content when different ayah is selected`() = runTest {
        // Given
        val testViewModel = createTestViewModel()
        testViewModel.onAyahLongPress(FIRST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // When
        testViewModel.onAyahLongPress(SECOND_AYAH_CONTENT, SECOND_AYAH_INDEX)

        // Then
        assertEquals(SECOND_AYAH_CONTENT, testViewModel.uiState.value.selectedAyah)
        assertEquals(SECOND_AYAH_INDEX, testViewModel.uiState.value.selectedAyahIndex)
    }

    @Test
    fun `onAyahLongPress should set selectedAyahIndex to negative value when called with negative index`() =
        runTest {
            // Given
            val testViewModel = createTestViewModel()

            // When
            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, NEGATIVE_AYAH_INDEX)

            // Then
            assertEquals(NEGATIVE_AYAH_INDEX, testViewModel.uiState.value.selectedAyahIndex)
        }

    @Test
    fun `onDismissActionButtons should hide action buttons when it called`() =
        runTest {
            // Given
            val testViewModel = createTestViewModel()
            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

            // When
            testViewModel.onDismissActionButtons()

            // Then
            assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
        }

    @Test
    fun `onDismissActionButtons should clear selectedAyah when called`() =
        runTest {
            // Given
            val testViewModel = createTestViewModel()
            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

            // When
            testViewModel.onDismissActionButtons()

            // Then
            assertEquals(EMPTY_STRING, testViewModel.uiState.value.selectedAyah)
        }

    @Test
    fun `onBookmarkClick should hide action buttons after bookmark click`() = runTest {
        // Given
        val testViewModel = createTestViewModel()
        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // When
        testViewModel.onBookmarkClick(TEST_AYAH_NUMBER)

        // Then
        assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onBookmarkClick should hide action buttons when ayah number is negative`() = runTest {
        // Given
        val testViewModel = createTestViewModel()
        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // When
        testViewModel.onBookmarkClick(ZERO_AYAH_NUMBER)

        // Then
        assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onShareClick should hide action buttons after share click`() = runTest {
        // Given
        val testViewModel = createTestViewModel()
        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        // When
        testViewModel.onShareClick(TEST_AYAH_CONTENT)

        // Then
        assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onShareClick should update selectedAyah with ayah content when called`() = runTest {
        // Given
        val testViewModel = createTestViewModel()

        // When
        testViewModel.onShareClick(AYAH_TO_SHARE)

        // Then
        assertEquals(AYAH_TO_SHARE, testViewModel.uiState.value.selectedAyah)
    }

    @Test
    fun `onShareClick should navigate to ShareAyah when onShareClick is invoked`() = runTest {
        // Given
        val testViewModel = createTestViewModel()

        // When & Then
        testViewModel.uiEffect.test {
            testViewModel.onShareClick(AYAH_TO_SHARE)
            assertEquals(SurahScreenEffect.ShareAyah(AYAH_TO_SHARE), awaitItem())
        }
    }

    @Test
    fun `onAyahLongPress should keep previous selectedAyah when called with same content`() =
        runTest {
            // Given
            val testViewModel = createTestViewModel()
            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

            // When
            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, SECOND_AYAH_INDEX)

            // Then
            assertEquals(TEST_AYAH_CONTENT, testViewModel.uiState.value.selectedAyah)
        }

    @Test
    fun `onAyahLongPress should update selectedAyahIndex when called with different index`() =
        runTest {
            // Given
            val testViewModel = createTestViewModel()
            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

            // When
            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, SECOND_AYAH_INDEX)

            // Then
            assertEquals(SECOND_AYAH_INDEX, testViewModel.uiState.value.selectedAyahIndex)
        }

    @Test
    fun `showSuccessSnackBar should display success status when called`() = runTest {
        // Given
        val testViewModel = createTestViewModel()

        // When & Then
        testViewModel.snackBarState.test {
            testViewModel.onCopyClick(AYAH_TO_COPY)
            val snackBarState = awaitItem()
            assertEquals(SnackBarState.Status.Success, snackBarState.status)
        }
    }

    @Test
    fun `onCopyClick should update state correctly when copy operation succeeds`() = runTest {
        // Given
        val testViewModel = createTestViewModel()
        testViewModel.onAyahLongPress(PREVIOUS_CONTENT, SELECTED_INDEX)

        // When
        testViewModel.onCopyClick(AYAH_CONTENT)
        Dispatchers.setMain(testDispatcher)

        // Then
        val testState = testViewModel.uiState.value
        assertEquals(SELECTED_INDEX, testState.selectedAyahIndex)
    }

    private companion object {
        const val DEFAULT_SURAH_ID = 1
        const val DEFAULT_SURAH_NAME = "Al-Fatiha"
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
        const val AYAH_CONTENT = "Test ayah content"
        const val PREVIOUS_CONTENT = "Previous content"
        const val SELECTED_INDEX = 5
        const val AYAH_TO_COPY = "Ayah to copy"
    }
}
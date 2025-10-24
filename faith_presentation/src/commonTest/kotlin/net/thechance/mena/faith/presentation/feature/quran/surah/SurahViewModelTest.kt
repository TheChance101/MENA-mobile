package net.thechance.mena.faith.presentation.feature.quran.surah

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import net.thechance.mena.faith.presentation.feature.quran.surah.args.SurahArgs
import net.thechance.mena.faith.presentation.utils.ClipboardManager
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SurahViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testViewModel: SurahViewModel
    private val quranRepository: QuranRepository = mock(mode = MockMode.autofill)
    private val bookmarkRepository: BookmarkRepository = mock(mode = MockMode.autofill)
    private val clipboardManager: ClipboardManager = mock(mode = MockMode.autofill)
    private val snackbarHandler: SnackbarHandler = mock(mode = MockMode.autofill)

    private val surahArgs = mock<SurahArgs>(mode = MockMode.autofill)

    @BeforeTest
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        testViewModel = SurahViewModel(
            surahArgs = surahArgs,
            dispatcher = testDispatcher,
            quranRepository = quranRepository,
            clipboardManager = clipboardManager,
            bookmarkRepository = bookmarkRepository,
            snackbarHandler = SnackbarHandler.Empty
        )
    }

    @Test
    fun `onBackClick should navigate back when it called`() = runTest {
        // Given & When & Then
        testViewModel.uiEffect.test {
            testViewModel.onBackClick()
            val effect = awaitItem()
            assertEquals(SurahScreenEffect.NavigateBack, effect)
        }
    }

    @Test
    fun `onAyahLongPress should show action buttons when it called`() = runTest {
        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        assertTrue(testViewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onAyahLongPress should return ayah content for selectedAyah when called`() = runTest {
        testViewModel.onAyahLongPress(SELECTED_AYAH_CONTENT, TEST_AYAH_INDEX)

        assertEquals(SELECTED_AYAH_CONTENT, testViewModel.uiState.value.selectedAyah)
    }

    @Test
    fun `onAyahLongPress should return ayah index for selectedAyahIndex when called`() = runTest {
        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        assertEquals(TEST_AYAH_INDEX, testViewModel.uiState.value.selectedAyahNumber)
    }

    @Test
    fun `onAyahLongPress should return new content when different ayah is selected`() = runTest {
        testViewModel.onAyahLongPress(FIRST_AYAH_CONTENT, TEST_AYAH_INDEX)

        testViewModel.onAyahLongPress(SECOND_AYAH_CONTENT, SECOND_AYAH_INDEX)

        assertEquals(SECOND_AYAH_CONTENT, testViewModel.uiState.value.selectedAyah)
        assertEquals(SECOND_AYAH_INDEX, testViewModel.uiState.value.selectedAyahNumber)
    }

    @Test
    fun `onAyahLongPress should set selectedAyahIndex to zero value when called with negative index`() =
        runTest {
            everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat

            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, NEGATIVE_AYAH_INDEX)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(0, testViewModel.uiState.value.selectedAyahNumber)
        }

    @Test
    fun `onDismissActionButtons should hide action buttons when it called`() =
        runTest {
            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

            testViewModel.onDismissActionButtons()

            assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
        }

    @Test
    fun `onDismissActionButtons should clear selectedAyah when called`() =
        runTest {
            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

            testViewModel.onDismissActionButtons()

            assertEquals(EMPTY_STRING, testViewModel.uiState.value.selectedAyah)
        }

    @Test
    fun `onBookmarkClick should hide action buttons after bookmark click`() = runTest {
        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        testViewModel.onBookmarkClick(TEST_AYAH_NUMBER)

        assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onBookmarkClick should hide action buttons when ayah number is negative`() = runTest {
        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        testViewModel.onBookmarkClick(ZERO_AYAH_NUMBER)

        assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onShareClick should hide action buttons after share click`() = runTest {
        everySuspend { quranRepository.getAyatOfSurah(DEFAULT_SURAH_ID) } returns dummyAyat
        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        testViewModel.onShareClick(TEST_AYAH_CONTENT)
        advanceUntilIdle()

        assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onShareClick should update selectedAyah with ayah content when called`() = runTest {
        testViewModel.onShareClick(AYAH_TO_SHARE)

        assertEquals(AYAH_TO_SHARE, testViewModel.uiState.value.selectedAyah)
    }

    @Test
    fun `onShareClick should navigate to ShareAyah when onShareClick is invoked`() = runTest {
        testViewModel.uiEffect.test {
            testViewModel.onShareClick(AYAH_TO_SHARE)
            assertEquals(SurahScreenEffect.ShareAyah(AYAH_TO_SHARE), awaitItem())
        }
    }

    @Test
    fun `onAyahLongPress should keep previous selectedAyah when called with same content`() =
        runTest {
            everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat
            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(TEST_AYAH_CONTENT, testViewModel.uiState.value.selectedAyah)
        }

    @Test
    fun `onAyahLongPress should update selectedAyahIndex when called with different index`() =
        runTest {
            everySuspend { quranRepository.getAyatOfSurah(any()) } returns emptyList()
            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, SECOND_AYAH_INDEX)

            assertEquals(SECOND_AYAH_INDEX, testViewModel.uiState.value.selectedAyahNumber)
        }

    @Test
    fun `showSuccessSnackBar should display success status when called`() = runTest {
        testViewModel.snackBarState.test {
            testViewModel.onCopyClick(AYAH_TO_COPY)
            val snackBarState = awaitItem()
            assertEquals(SnackBarState.Status.Success, snackBarState.status)
        }
    }

    @Test
    fun `onCopyClick should update state correctly when copy operation succeeds`() = runTest {
        everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat

        testViewModel.onCopyClick(AYAH_CONTENT)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(AYAH_CONTENT, testViewModel.uiState.value.selectedAyah)
    }

    @Test
    fun `onBookmarkClick should add bookmark successfully`() = runTest(testDispatcher) {
        everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat

        testViewModel = SurahViewModel(
            surahArgs = surahArgs,
            dispatcher = testDispatcher,
            quranRepository = quranRepository,
            clipboardManager = clipboardManager,
            bookmarkRepository = bookmarkRepository,
            snackbarHandler = snackbarHandler
        )
        advanceUntilIdle()

        testViewModel.onBookmarkClick(TEST_AYAH_NUMBER)
        advanceUntilIdle()

        assertEquals(false, testViewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onBookmarkClick should show success snackbar after adding bookmark`() =
        runTest(testDispatcher) {
            everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat

            testViewModel = SurahViewModel(
                surahArgs = surahArgs,
                dispatcher = testDispatcher,
                quranRepository = quranRepository,
                clipboardManager = clipboardManager,
                bookmarkRepository = bookmarkRepository,
                snackbarHandler = snackbarHandler
            )
            advanceUntilIdle()

            testViewModel.onBookmarkClick(TEST_AYAH_NUMBER)
            advanceUntilIdle()

            assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
        }


    @Test
    fun `onFirstVisibleAyahChanged should save last ayah for tilawah`() = runTest(testDispatcher) {
        everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat

        testViewModel = SurahViewModel(
            surahArgs = surahArgs,
            dispatcher = testDispatcher,
            quranRepository = quranRepository,
            clipboardManager = clipboardManager,
            bookmarkRepository = bookmarkRepository,
            snackbarHandler = snackbarHandler
        )
        advanceUntilIdle()

        testViewModel.updateContinueTilawah(TRACKED_AYAH_NUMBER)
        advanceUntilIdle()
    }

    @Test
    fun `onFirstVisibleAyahChanged should track ayah with correct surah id`() =
        runTest(testDispatcher) {
            everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat

            testViewModel = SurahViewModel(
                surahArgs = surahArgs,
                dispatcher = testDispatcher,
                quranRepository = quranRepository,
                clipboardManager = clipboardManager,
                bookmarkRepository = bookmarkRepository,
                snackbarHandler = snackbarHandler
            )
            advanceUntilIdle()

            testViewModel.updateContinueTilawah(TRACKED_AYAH_NUMBER)
            advanceUntilIdle()
        }


    @Test
    fun `updateContinueTilawah should include surah name in saved data`() =
        runTest(testDispatcher) {
            everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat

            testViewModel = SurahViewModel(
                surahArgs = surahArgs,
                dispatcher = testDispatcher,
                quranRepository = quranRepository,
                clipboardManager = clipboardManager,
                bookmarkRepository = bookmarkRepository,
                snackbarHandler = snackbarHandler
            )
            advanceUntilIdle()

            testViewModel.updateContinueTilawah(TRACKED_AYAH_NUMBER)
            advanceUntilIdle()

        }

    @Test
    fun `onCopyClick should hide action buttons after successful copy`() = runTest(testDispatcher) {
        everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat

        testViewModel = SurahViewModel(
            surahArgs = surahArgs,
            dispatcher = testDispatcher,
            quranRepository = quranRepository,
            clipboardManager = clipboardManager,
            bookmarkRepository = bookmarkRepository,
            snackbarHandler = snackbarHandler
        )
        advanceUntilIdle()

        testViewModel.onCopyClick(AYAH_CONTENT)
        advanceUntilIdle()

        assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onCopyClick should store copied ayah content in state`() = runTest(testDispatcher) {
        everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat

        testViewModel = SurahViewModel(
            surahArgs = surahArgs,
            dispatcher = testDispatcher,
            quranRepository = quranRepository,
            clipboardManager = clipboardManager,
            bookmarkRepository = bookmarkRepository,
            snackbarHandler = snackbarHandler
        )
        advanceUntilIdle()

        testViewModel.onCopyClick(AYAH_CONTENT)
        advanceUntilIdle()

        assertEquals(AYAH_CONTENT, testViewModel.uiState.value.selectedAyah)
    }

    @Test
    fun `updateContinueTilawah should save last ayah for tilawah correctly`() = runTest {
        every { surahArgs.surahId } returns SURAH_BAQARAH_ID
        every { surahArgs.surahName } returns SURAH_BAQARAH
        everySuspend { quranRepository.saveLastAyahForTilawah(any()) } returns Unit

        testViewModel.updateContinueTilawah(5)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(SURAH_BAQARAH_ID, surahArgs.surahId)
        assertEquals(SURAH_BAQARAH, surahArgs.surahName)
    }


    private companion object {
        const val TRACKED_AYAH_NUMBER = 5
        const val DEFAULT_SURAH_ID = 1
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
        const val AYAH_TO_COPY = "Test ayah to copy"
        const val SURAH_BAQARAH = "Al-Baqarah"
        const val SURAH_BAQARAH_ID = 2
        private val dummyAyat = listOf(
            Ayah(
                number = 1,
                surahId = 1,
                content = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                plainContent = "بسم الله الرحمن الرحيم"
            ),
            Ayah(
                number = 2,
                surahId = 1,
                content = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                plainContent = "الحمد لله العالمين"
            ),
            Ayah(
                number = 3,
                surahId = 1,
                content = "الرَّحْمَٰنِ الرَّحِيمِ",
                plainContent = "الرحمن الرحيم"
            ),
            Ayah(
                number = 4,
                surahId = 1,
                content = "مَالِكِ يَوْمِ الدِّينِ",
                plainContent = "مالك يوم الدين"
            )

        )
    }
}

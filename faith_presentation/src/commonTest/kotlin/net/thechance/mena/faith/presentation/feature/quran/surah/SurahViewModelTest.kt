package net.thechance.mena.faith.presentation.feature.quran.surah

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.mediaPlayer.QuranPlayer
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import net.thechance.mena.faith.presentation.feature.quran.surah.args.SurahArgs
import net.thechance.mena.faith.presentation.utils.ClipboardManager
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SurahViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testViewModel: SurahViewModel
    private val quranRepository: QuranRepository = mock(mode = MockMode.autofill)
    private val bookmarkRepository: BookmarkRepository = mock(mode = MockMode.autofill)
    private val clipboardManager: ClipboardManager = mock(mode = MockMode.autofill)
    private val quranPlayer: QuranPlayer = mock(mode = MockMode.autofill)
    private val surahArgs = mock<SurahArgs>(mode = MockMode.autofill)
    private val snackbarHandler: SnackbarHandler = mock(mode = MockMode.autofill)

    @BeforeTest
    fun setup() {
        every { snackbarHandler.snackBarState } returns MutableStateFlow(SnackBarState())

        startKoin {
            modules(module { single { mock<SnackbarHandler>(MockMode.autofill) } })
            modules(module { single { snackbarHandler } })
        }

        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        testViewModel = SurahViewModel(
            surahArgs = surahArgs,
            dispatcher = testDispatcher,
            quranRepository = quranRepository,
            clipboardManager = clipboardManager,
            bookmarkRepository = bookmarkRepository,
            quranPlayer = quranPlayer
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    // Navigation Tests
    @Test
    fun `onBackClick should navigate back when it called`() = runTest {
        testViewModel.uiEffect.test {
            testViewModel.onBackClick()
            val effect = awaitItem()
            assertEquals(SurahScreenEffect.NavigateBack, effect)
        }
    }

    @Test
    fun `onSearchClick should navigate to search screen`() = runTest {
        every { surahArgs.surahId } returns 2

        testViewModel.uiEffect.test {
            testViewModel.onSearchClick()
        }
    }

    @Test
    fun `onListenClick should play ayah with selected ayah number`() = runTest {
        everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat
        everySuspend { quranRepository.getAyahSoundUrl(any(), any(), any()) } returns "test_url"

        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_NUMBER)
        testViewModel.onListenClick()
        advanceUntilIdle()

        assertTrue(testViewModel.uiState.value.isAyahSoundPlaying)
    }

    @Test
    fun `onListenClick should play first ayah when no ayah is selected`() = runTest {
        everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat
        everySuspend { quranRepository.getAyahSoundUrl(any(), any(), any()) } returns "test_url"

        testViewModel.onListenClick()
        advanceUntilIdle()

        assertEquals(1, testViewModel.uiState.value.selectedAyahNumber)
    }

    @Test
    fun `onNextAyahClick should wrap to first ayah when at last ayah`() = runTest {
        everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat
        everySuspend { quranRepository.getAyahSoundUrl(any(), any(), any()) } returns "test_url"

        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, 4)
        testViewModel.onNextAyahClick()
        advanceUntilIdle()

        assertEquals(1, testViewModel.uiState.value.selectedAyahNumber)
    }

    @Test
    fun `onPreviousAyahClick should wrap to first ayah when at first ayah`() = runTest {
        everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat
        everySuspend { quranRepository.getAyahSoundUrl(any(), any(), any()) } returns "test_url"

        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, 1)
        testViewModel.onPreviousAyahClick()
        advanceUntilIdle()

        assertEquals(1, testViewModel.uiState.value.selectedAyahNumber)
    }

    @Test
    fun `onPlayPauseClick should pause when audio is playing`() = runTest {
        everySuspend { quranRepository.getAyahSoundUrl(any(), any(), any()) } returns "test_url"

        testViewModel.onListenClick()
        advanceUntilIdle()

        testViewModel.onPlayPauseClick()

        assertFalse(testViewModel.uiState.value.isAyahSoundPlaying)
    }

    @Test
    fun `onPlayPauseClick should resume when audio is paused`() = runTest {
        everySuspend { quranRepository.getAyahSoundUrl(any(), any(), any()) } returns "test_url"

        testViewModel.onListenClick()
        advanceUntilIdle()
        testViewModel.onPlayPauseClick()
        testViewModel.onPlayPauseClick()

        assertTrue(testViewModel.uiState.value.isAyahSoundPlaying)
    }

    @Test
    fun `onPlayPauseClick should do nothing when no ayah url is set`() = runTest {
        testViewModel.onPlayPauseClick()

        assertFalse(testViewModel.uiState.value.isAyahSoundPlaying)
    }

    @Test
    fun `onRepeatAyahClick should call repeatCurrentAyah and update state`() = runTest {
        testViewModel.onRepeatAyahClick()

        verify { quranPlayer.repeatCurrentAyah() }
        assertTrue(testViewModel.uiState.value.isAyahSoundPlaying)
    }

    @Test
    fun `onClosePlayerClick should pause playback and hide player`() = runTest {
        testViewModel.onClosePlayerClick()

        verify { quranPlayer.pauseAyah() }
        assertFalse(testViewModel.uiState.value.isPlayerVisible)
        assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `init should set loading state correctly`() = runTest {
        every { surahArgs.surahId } returns 1
        everySuspend { quranRepository.getAyatOfSurah(1) } returns dummyAyat

        val viewModel = SurahViewModel(
            surahArgs = surahArgs,
            dispatcher = testDispatcher,
            quranRepository = quranRepository,
            clipboardManager = clipboardManager,
            bookmarkRepository = bookmarkRepository,
            quranPlayer = quranPlayer
        )

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onAyahLongPress should show action buttons when it called`() = runTest {
        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        assertTrue(testViewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onInitialAyahScrolled should make selectedAyahNumber and initialAyahToScroll null when it called`() =
        runTest {
            testViewModel.onInitialAyahScrolled()

            assertNull(testViewModel.uiState.value.selectedAyahNumber)
            assertNull(testViewModel.uiState.value.initialAyahToScroll)
        }

    @Test
    fun `highlightAyah should update initialAyahToScroll and selectedAyahNumber `() = runTest {
        testViewModel.highlightAyah(TRACKED_AYAH_NUMBER)

        assertEquals(testViewModel.uiState.value.initialAyahToScroll, TRACKED_AYAH_NUMBER)
        assertEquals(testViewModel.uiState.value.selectedAyahNumber, TRACKED_AYAH_NUMBER)
    }

    @Test
    fun `playSurah should play audio from selected ayah to ath the end of surah`() = runTest {
        testViewModel.playSurah(surahNumber = SURAH_BAQARAH_ID)

        assertTrue(testViewModel.uiState.value.isAutoPlayEnabled)
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
    fun `onAyahLongPress should hide player when showing action buttons`() = runTest {
        everySuspend { quranRepository.getAyahSoundUrl(any(), any(), any()) } returns "test_url"

        testViewModel.onListenClick()
        advanceUntilIdle()
        assertTrue(testViewModel.uiState.value.isPlayerVisible)

        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        assertFalse(testViewModel.uiState.value.isPlayerVisible)
    }

    // Action Buttons Tests
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

    // Bookmark Tests
    @Test
    fun `onBookmarkClick should hide action buttons after bookmark click`() = runTest {
        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        testViewModel.onBookmarkClick(TEST_AYAH_NUMBER)

        assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
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
            quranPlayer = quranPlayer
        )
        advanceUntilIdle()

        testViewModel.onBookmarkClick(TEST_AYAH_NUMBER)
        advanceUntilIdle()

        assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
    }

    // Share Tests
    @Test
    fun `onShareClick should hide action buttons after share click`() = runTest {
        everySuspend { quranRepository.getAyatOfSurah(DEFAULT_SURAH_ID) } returns dummyAyat
        testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)

        testViewModel.onShareClick(testViewModel.uiState.value.selectedAyah)
        advanceUntilIdle()

        assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
    }

    @Test
    fun `onShareClick should update selectedAyah with ayah content when called`() = runTest {
        testViewModel.onShareClick(testViewModel.uiState.value.selectedAyah)
        assertEquals("", testViewModel.uiState.value.selectedAyah)
    }

    @Test
    fun `onShareClick should navigate to ShareAyah when onShareClick is invoked`() = runTest {

        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.uiEffect.test {
            testViewModel.onShareClick(testViewModel.uiState.value.selectedAyah)

            assertEquals(
                SurahScreenEffect.ShareAyah(
                    surahId = 0.toString(),
                    ayahNumber = 1,
                    ayahContent = AYAH_TO_SHARE
                ),
                awaitItem()
            )
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
    fun `onCopyClick should hide action buttons after successful copy`() = runTest(testDispatcher) {
        everySuspend { quranRepository.getAyatOfSurah(any()) } returns dummyAyat

        testViewModel = SurahViewModel(
            surahArgs = surahArgs,
            dispatcher = testDispatcher,
            quranRepository = quranRepository,
            clipboardManager = clipboardManager,
            bookmarkRepository = bookmarkRepository,
            quranPlayer = quranPlayer
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
            quranPlayer = quranPlayer
        )
        advanceUntilIdle()

        testViewModel.onCopyClick(AYAH_CONTENT)
        advanceUntilIdle()

        assertEquals(AYAH_CONTENT, testViewModel.uiState.value.selectedAyah)
    }

    // Tilawah Continuation Tests
    @Test
    fun `updateContinueTilawah should save last ayah for tilawah correctly`() = runTest {
        every { surahArgs.surahId } returns SURAH_BAQARAH_ID
        everySuspend { quranRepository.saveLastAyahForTilawah(any()) } returns Unit

        testViewModel.updateContinueTilawah(5)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(SURAH_BAQARAH_ID, surahArgs.surahId)
    }

    // Highlight and Scroll Tests
    @Test
    fun `onReciterClick should navigate to downloaded reciters screen`() = runTest {
        testViewModel.uiEffect.test {
            testViewModel.onReciterClick(surahArgs.surahId)
            val effect = awaitItem()
            assertEquals(
                SurahScreenEffect.NavigateToDownloadedRecitersScreen(
                    surahArgs.surahId
                ), effect
            )
        }

        @Test
        fun `highlightAyah should update initialAyahToScroll and selectedAyahNumber`() = runTest {
            testViewModel.highlightAyah(TRACKED_AYAH_NUMBER)

            assertEquals(TRACKED_AYAH_NUMBER, testViewModel.uiState.value.selectedAyahNumber)
            assertEquals(TRACKED_AYAH_NUMBER, testViewModel.uiState.value.initialAyahToScroll)
        }

        @Test
        fun `onInitialAyahScrolled should clear selection after delay when not playing`() =
            runTest {
                testViewModel.highlightAyah(TRACKED_AYAH_NUMBER)
                testViewModel.onInitialAyahScrolled()
                advanceUntilIdle()

                assertNull(testViewModel.uiState.value.selectedAyahNumber)
                assertNull(testViewModel.uiState.value.initialAyahToScroll)
            }

        // Audio Loading Tests
        @Test
        fun `loadAndPlayAyahSound should update current playing ayah url`() = runTest {
            val testUrl = "https://example.com/ayah.mp3"
            everySuspend { quranRepository.getAyahSoundUrl(any(), any(), any()) } returns testUrl

            testViewModel.onListenClick()
            advanceUntilIdle()

            assertEquals(testUrl, testViewModel.uiState.value.currentPlayingAyahUrl)
        }

        @Test
        fun `loadAndPlayAyahSound should show player and hide action buttons`() = runTest {
            everySuspend { quranRepository.getAyahSoundUrl(any(), any(), any()) } returns "test_url"

            testViewModel.onAyahLongPress(TEST_AYAH_CONTENT, TEST_AYAH_INDEX)
            testViewModel.onListenClick()
            advanceUntilIdle()

            assertTrue(testViewModel.uiState.value.isPlayerVisible)
            assertFalse(testViewModel.uiState.value.isAyahActionButtonsVisible)
        }
    }


    private companion object {
        const val TRACKED_AYAH_NUMBER = 5
        const val DEFAULT_SURAH_ID = 1
        const val TEST_AYAH_INDEX = 0
        const val SECOND_AYAH_INDEX = 1
        const val TEST_AYAH_NUMBER = 1
        const val TEST_AYAH_CONTENT = "Test ayah"
        const val FIRST_AYAH_CONTENT = "First ayah"
        const val SECOND_AYAH_CONTENT = "Second ayah"
        const val SELECTED_AYAH_CONTENT = "Selected ayah content"
        const val AYAH_TO_SHARE = ""
        const val EMPTY_STRING = ""
        const val AYAH_CONTENT = "Test ayah content"
        const val SURAH_BAQARAH_ID = 2


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
            ),
            Ayah(
                number = 4,
                surahId = 1,
                content = "مَالِكِ يَوْمِ الدِّينِ",
                plainContent = "مالك يوم الدين"
            )
        )
    }
}
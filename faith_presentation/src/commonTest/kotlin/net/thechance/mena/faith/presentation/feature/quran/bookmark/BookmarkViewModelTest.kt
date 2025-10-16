package net.thechance.mena.faith.presentation.feature.quran.bookmark

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private var repository: BookmarkRepository = mock(MockMode.autofill)
    private lateinit var viewModel: BookmarkViewModel
    private lateinit var snackbarHandler: SnackbarHandler

    @BeforeTest
    fun setUp() {
        testDispatcher  = StandardTestDispatcher()
        snackbarHandler = mock(MockMode.autofill)
        viewModel = BookmarkViewModel(
            bookmarkRepository = repository,
            dispatcher = testDispatcher,
            snackbarHandler = snackbarHandler
        )
    }

    @Test
    fun `init should load bookmarks successfully`() = runTest {
        // Given
        everySuspend { repository.getAyahBookmarks(any(), any()) } returns fakeBookmarks

        // When
        viewModel = BookmarkViewModel(
            bookmarkRepository = repository,
            dispatcher = testDispatcher,
            snackbarHandler = snackbarHandler
        )
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            val bookmarks = state.bookmarks.asSnapshot()

            assertEquals(2, bookmarks.size)
            assertEquals(BOOKMARK_ID1, bookmarks[0].bookmarkId)
            assertEquals(BOOKMARK_ID2, bookmarks[1].bookmarkId)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init should handle empty bookmarks list`() = runTest {
        // Given
        everySuspend { repository.getAyahBookmarks(any(), any()) } returns emptyList()

        // When
        viewModel = BookmarkViewModel(bookmarkRepository = repository, dispatcher = testDispatcher, snackbarHandler = snackbarHandler)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            val bookmarks = state.bookmarks.asSnapshot()

            assertEquals(0, bookmarks.size)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDeleteBookmarkClick should restore bookmark on error`() = runTest {
        // Given
        val exception = Exception("Delete failed")
        everySuspend { repository.getAyahBookmarks(any(), any()) } returns fakeBookmarks
        everySuspend { repository.deleteAyahBookmark(BOOKMARK_ID1) } throws exception

        viewModel = BookmarkViewModel(bookmarkRepository = repository, dispatcher = testDispatcher, snackbarHandler = snackbarHandler)
        advanceUntilIdle()

        // When
        viewModel.onDeleteBookmarkClick(bookmarkId = BOOKMARK_ID1)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            val bookmarks = state.bookmarks.asSnapshot()

            assertEquals(2, bookmarks.size)
            assertTrue(bookmarks.any { it.bookmarkId == BOOKMARK_ID1 })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBackClick should emit NavigateBack effect`() = runTest(testDispatcher) {
        // When & Then
        viewModel.uiEffect.test {
            viewModel.onBackClick()
            val effect = awaitItem()
            assertEquals(BookmarkEffect.NavigateBack, effect)
        }
    }

    @Test
    fun `onStartTilawahClick should emit NavigateBack effect`() = runTest(testDispatcher) {
        // When & Then
        viewModel.uiEffect.test {
            viewModel.onStartTilawahClick()
            assertEquals(BookmarkEffect.NavigateBack, awaitItem())
        }
    }

    private companion object {
        const val BOOKMARK_ID1 = 1
        const val BOOKMARK_ID2 = 2
        const val SURAH_ID = 1
        const val AYAH_NUMBER1 = 1
        const val AYAH_NUMBER2 = 2
        const val SURAH_AYAH_COUNT = 7

        @OptIn(ExperimentalTime::class)
        val fakeBookmarks: List<AyahBookmark> = listOf(
            AyahBookmark(
                id = BOOKMARK_ID1,
                surah = Surah(
                    id = SURAH_ID,
                    order = Surah.SurahOrder.AlFath,
                    name = "Al-Fatiha",
                    ayahCount = SURAH_AYAH_COUNT,
                ),
                ayah = Ayah(
                    number = AYAH_NUMBER1,
                    content = "Ayah 1 content",
                    surahId = SURAH_ID,
                    plainContent = "Ayah 1 plain content"
                ),
                createdAt = Instant.fromEpochMilliseconds(0)
            ),
            AyahBookmark(
                id = BOOKMARK_ID2,
                surah = Surah(
                    id = SURAH_ID,
                    order = Surah.SurahOrder.AlFath,
                    name = "Al-Fatiha",
                    ayahCount = SURAH_AYAH_COUNT,
                ),
                ayah = Ayah(
                    number = AYAH_NUMBER2,
                    content = "Ayah 2 content",
                    surahId = SURAH_ID,
                    plainContent = "Ayah 2 plain content"
                ),
                createdAt = Instant.fromEpochMilliseconds(0)
            )
        )
    }
}

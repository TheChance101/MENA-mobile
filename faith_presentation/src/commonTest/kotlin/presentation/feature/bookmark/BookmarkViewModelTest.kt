package presentation.feature.bookmark

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.faith.presentation.feature.quran.bookmark.BookmarkEffect
import net.thechance.mena.faith.presentation.feature.quran.bookmark.BookmarkViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkViewModelTest {

    private val repository: BookmarkRepository = mock(MockMode.autofill)

    @Test
    fun `init should load bookmarks when viewModel is created`() = runTest {
        everySuspend { repository.getAllAyahBookmarks() } returns fakeBookmarks

        val viewModel = BookmarkViewModel(repository)

        advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(BOOKMARK_ID, state.bookmarks.size)
            assertEquals(BOOKMARK_ID, state.bookmarks.first().bookmarkId)
        }
    }

    @Test
    fun `onDeleteBookmarkClick should remove bookmark from state when repository succeeds`() =
        runTest {
            everySuspend { repository.getAllAyahBookmarks() } returns fakeBookmarks
            everySuspend { repository.deleteAyahBookmark(BOOKMARK_ID) } returns Unit

            val viewModel = BookmarkViewModel(repository)
            viewModel.onDeleteBookmarkClick(BOOKMARK_ID)

            advanceUntilIdle()
            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue(state.bookmarks.isEmpty())
            }
            verifySuspend { repository.deleteAyahBookmark(BOOKMARK_ID) }
        }

    @Test
    fun `onDeleteBookmarkClick should keep bookmarks when repository fails`() = runTest {
        everySuspend { repository.getAllAyahBookmarks() } returns fakeBookmarks
        everySuspend { repository.deleteAyahBookmark(BOOKMARK_ID) } throws RuntimeException("delete failed")

        val viewModel = BookmarkViewModel(repository)

        viewModel.onDeleteBookmarkClick(BOOKMARK_ID)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.bookmarks.size)
        }
    }

    @Test
    fun `onBackClick should navigate back`() = runTest {
        val viewModel = BookmarkViewModel(repository)

        viewModel.uiEffect.test {
            viewModel.onBackClick()
            assertEquals(BookmarkEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onStartTilawahClick should emit NavigateBack effect`() = runTest {
        val viewModel = BookmarkViewModel(repository)

        viewModel.uiEffect.test {
            viewModel.onStartTilawahClick()
            assertEquals(BookmarkEffect.NavigateBack, awaitItem())
        }
    }

    private companion object FakeData {
        const val BOOKMARK_ID = 1
        const val SURAH_ID = 1
        const val AYAH_NUMBER = 1
        const val SURAH_AYAH_COUNT = 7

        @OptIn(ExperimentalTime::class)
        val fakeBookmarks = listOf(
            AyahBookmark(
                id = BOOKMARK_ID,
                surah = Surah(
                    id = SURAH_ID,
                    order = Surah.SurahOrder.AlFatihah,
                    name = "Al-Fatihah",
                    ayahCount = SURAH_AYAH_COUNT,
                    isMakkia = true
                ),
                ayah = Ayah(
                    number = AYAH_NUMBER,
                    surahId = SURAH_ID,
                    content = "بسم الله الرحمن الرحيم"
                ),
                createdAt = Instant.DISTANT_PAST
            )
        )
    }
}

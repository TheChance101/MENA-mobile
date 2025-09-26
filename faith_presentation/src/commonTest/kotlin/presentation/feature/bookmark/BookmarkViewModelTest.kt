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

class BookmarkViewModelTest {

    private val repository: BookmarkRepository = mock(MockMode.autofill)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init should load bookmarks when viewModel is created`() = runTest {
        everySuspend { repository.getAllAyahBookmarks() } returns FakeData.fakeBookmarks

        val viewModel = BookmarkViewModel(repository)

        advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.bookmarks.size)
            assertEquals(1, state.bookmarks.first().bookmarkId)
        }

        verifySuspend { repository.getAllAyahBookmarks() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onDeleteBookmarkClick should remove bookmark from state when repository succeeds`() =
        runTest {
            everySuspend { repository.getAllAyahBookmarks() } returns FakeData.fakeBookmarks
            everySuspend { repository.deleteAyahBookmark(1) } returns Unit

            val viewModel = BookmarkViewModel(repository)
            viewModel.onDeleteBookmarkClick(1)

            advanceUntilIdle()
            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue(state.bookmarks.isEmpty())
            }
            verifySuspend { repository.deleteAyahBookmark(1) }
        }


    @Test
    fun `onDeleteBookmarkClick should keep bookmarks when repository fails`() = runTest {
        everySuspend { repository.getAllAyahBookmarks() } returns FakeData.fakeBookmarks
        everySuspend { repository.deleteAyahBookmark(1) } throws RuntimeException("delete failed")

        val viewModel = BookmarkViewModel(repository)

        viewModel.onDeleteBookmarkClick(1)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.bookmarks.size)
        }
    }

    @Test
    fun `onBackClick should emit NavigateBack effect`() = runTest {
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
        @OptIn(ExperimentalTime::class)
        val fakeBookmarks = listOf(
            AyahBookmark(
                id = 1,
                surah = Surah(
                    id = 1,
                    order = Surah.SurahOrder.AlFatihah,
                    name = "Al-Fatihah",
                    ayahCount = 7,
                    isMakkia = true
                ),
                ayah = Ayah(
                    number = 1,
                    surahId = 1,
                    content = "بسم الله الرحمن الرحيم"
                ),
                createdAt = Instant.DISTANT_PAST
            )
        )
    }
}
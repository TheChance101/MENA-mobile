package net.thechance.mena.faith.presentation.feature.quran.bookmark

import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.bookmark_removed_successfully
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.base.SnackBarState

class BookmarkViewModel(
    private val bookmarkRepository: BookmarkRepository
) : BaseViewModel<BookmarksScreenState, BookmarkEffect>(BookmarksScreenState()),
    BookmarkInteractionListener {

    init {
        getBookmarks()
    }

    override fun onBackClick() = sendEffect(BookmarkEffect.NavigateBack)

    override fun onStartTilawahClick() = sendEffect(BookmarkEffect.NavigateBack)

    override fun onDeleteBookmarkClick(bookmarkId: Int) {
        tryToExecute(
            execute = { bookmarkRepository.deleteAyahBookmark(bookmarkId) },
            onSuccess = { onDeleteBookmarkSuccess(bookmarkId) },
            onError = ::handleErrorState,
        )
    }

    private fun getBookmarks() {
        tryToExecute(
            execute = { bookmarkRepository.getAllAyahBookmarks() },
            onSuccess = ::onGetBookmarksSuccess,
            onError = ::handleErrorState,
        )
    }

    private fun onDeleteBookmarkSuccess(bookmarkId: Int) {
        updateState { currentState ->
            currentState.copy(
                bookmarks = currentState.bookmarks.filterNot { it.bookmarkId == bookmarkId }
            )
        }
        showSnackBar(
            message = Res.string.bookmark_removed_successfully,
            status = SnackBarState.Status.Success
        )
    }

    private fun onGetBookmarksSuccess(ayahBookmarks: List<AyahBookmark>) {
        updateState {
            it.copy(bookmarks = ayahBookmarks.map { bookmark -> bookmark.toUiState() })
        }
    }

    private fun handleErrorState(throwable: Throwable) {
        // TODO: handle error here
    }

}

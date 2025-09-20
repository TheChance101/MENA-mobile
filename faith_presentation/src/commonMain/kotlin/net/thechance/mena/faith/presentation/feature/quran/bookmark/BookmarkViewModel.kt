package net.thechance.mena.faith.presentation.feature.quran.bookmark

import net.thechance.mena.faith.domain.entity.Bookmark
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel

class BookmarkViewModel(
    private val bookmarkRepository: BookmarkRepository
) : BaseViewModel<BookmarksScreenState, BookmarkEffect>(BookmarksScreenState()),
    BookmarkInteractionListener {

    init {
        getBookmarks()
    }

    override fun onBackClick() = sendEffect(BookmarkEffect.NavigateBack)

    override fun onStartTilawahClick() = sendEffect(BookmarkEffect.NavigateToQuran)

    override fun onRemoveBookmarkClick(bookmarkId: Int) {
        tryToExecute(
            onStart = { setLoadingState(true) },
            execute = { bookmarkRepository.removeBookmark(bookmarkId) },
            onSuccess = { removeBookmarkFromState(bookmarkId) },
            onError = { ::handleErrorState },
            onFinally = { setLoadingState(false) }
        )
    }

    private fun getBookmarks() {
        tryToExecute(
            onStart = { setLoadingState(true) },
            execute = { bookmarkRepository.getAllBookmarks() },
            onSuccess = ::handleSuccessState,
            onError = ::handleErrorState,
            onFinally = { setLoadingState(false) }
        )
    }

    private fun handleSuccessState(bookmarks: List<Bookmark>) {
        updateState {
            it.copy(bookmarks = bookmarks.map { bookmark -> bookmark.toUiState() })
        }
    }

    private fun handleErrorState(throwable: Throwable) {
        updateState { it.copy(error = "${throwable.message}") }
    }

    private fun setLoadingState(isLoading: Boolean) {
        updateState { it.copy(isLoading = isLoading) }
    }

    private fun removeBookmarkFromState(bookmarkId: Int) {
        updateState { currentState ->
            currentState.copy(
                bookmarks = currentState.bookmarks.filter { it.bookmarkId != bookmarkId }
            )
        }
    }

}

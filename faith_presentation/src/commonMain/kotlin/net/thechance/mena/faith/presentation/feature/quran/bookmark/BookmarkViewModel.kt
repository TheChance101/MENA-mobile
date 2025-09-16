package net.thechance.mena.faith.presentation.feature.quran.bookmark

import net.thechance.mena.faith.domain.entity.Bookmark
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel

class BookmarkViewModel(
    private val bookmarkRepository: BookmarkRepository
) : BaseViewModel<BookmarksScreenState, BookmarkEffect>(BookmarksScreenState()),
    BookmarkInteractionListener {

    init {
        initializeBookmarks()
    }

    private fun initializeBookmarks() {
        tryToExecute(
            onStart = { setLoadingState(true) },
            execute = { bookmarkRepository.getAllBookmarks() },
            onSuccess = { bookmarks -> handleSuccessState(bookmarks) },
            onError = { throwable -> handleErrorState(throwable) },
            onFinally = { setLoadingState(false) }
        )
    }

    private fun handleSuccessState(bookmarks: List<Bookmark>) {
        updateState {
            it.copy(bookmarks = bookmarks.map { bookmark -> bookmark.toUiState() })
        }
    }

    override fun onRemoveBookmarkClick(id: Int) {
        tryToExecute(
            onStart = { setLoadingState(true) },
            execute = { bookmarkRepository.removeBookmark(id) },
            onSuccess = { removeBookmarkFromState(id) },
            onError = { throwable -> handleErrorState(throwable) },
            onFinally = { setLoadingState(false) }
        )
    }

    private fun removeBookmarkFromState(bookmarkId: Int) {
        updateState { currentState ->
            currentState.copy(
                bookmarks = currentState.bookmarks.filter { it.bookmarkId != bookmarkId }
            )
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        updateState { it.copy(isLoading = isLoading) }
    }

    private fun handleErrorState(throwable: Throwable) {
        updateState { it.copy(error = "${throwable.message}") }
    }

    override fun onBackClick() = sendEffect(BookmarkEffect.NavigateBack)

}

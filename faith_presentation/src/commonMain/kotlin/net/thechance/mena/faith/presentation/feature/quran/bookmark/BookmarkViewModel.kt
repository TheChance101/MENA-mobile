package net.thechance.mena.faith.presentation.feature.quran.bookmark

import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.bookmark_removed_successfully
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.base.SnackBarState
import net.thechance.mena.faith.presentation.base.createPagingSourceFlow

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
            execute = { createBookmarksPagingSource() },
            onStart = { updateState { it.copy(isLoading = true) } },
            onSuccess = ::onGetBookmarksSuccess,
            onError = ::handleErrorState,
        )
    }

    private fun onDeleteBookmarkSuccess(bookmarkId: Int) {
        filterPagingData(bookmarkId)

        showSnackBar(
            message = Res.string.bookmark_removed_successfully,
            status = SnackBarState.Status.Success
        )
    }

    private fun onGetBookmarksSuccess(ayahBookmarksFlow: Flow<PagingData<AyahBookmark>>) {
        updateState {
            it.copy(
                bookmarks = ayahBookmarksFlow.map { pagingData ->
                    pagingData.map { bookmark -> bookmark.toUiState() }
                },
                isLoading = false
            )
        }
    }

    private fun filterPagingData(bookmarkId: Int) {
        updateState { currentState ->
            currentState.copy(
                bookmarks = currentState.bookmarks.map { pagingData ->
                    pagingData.filter { it ->
                        it.bookmarkId != bookmarkId
                    }
                }
            )
        }
    }

    private fun createBookmarksPagingSource(): Flow<PagingData<AyahBookmark>> =
        createPagingSourceFlow { _, pageNumber -> bookmarkRepository.getAyahBookmarks(pageNumber) }

    private fun handleErrorState(throwable: Throwable) {
        // TODO: handle error here
    }

}

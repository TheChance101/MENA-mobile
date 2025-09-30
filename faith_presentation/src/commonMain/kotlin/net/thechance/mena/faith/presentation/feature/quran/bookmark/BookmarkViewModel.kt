package net.thechance.mena.faith.presentation.feature.quran.bookmark

import androidx.lifecycle.viewModelScope
import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import app.cash.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
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

    private val deletedBookmarkIds = MutableStateFlow<Set<Int>>(emptySet())

    init {
        getBookmarks()
    }

    override fun onBackClick() = sendEffect(BookmarkEffect.NavigateBack)

    override fun onStartTilawahClick() = sendEffect(BookmarkEffect.NavigateBack)

    override fun onDeleteBookmarkClick(bookmarkId: Int) {
        deletedBookmarkIds.update { it + bookmarkId }

        tryToExecute(
            execute = { bookmarkRepository.deleteAyahBookmark(bookmarkId) },
            onSuccess = { onDeleteBookmarkSuccess() },
            onError = { error ->
                deletedBookmarkIds.update { it - bookmarkId }
                handleErrorState(error)
            },
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

    private fun onDeleteBookmarkSuccess() {
        showSnackBar(
            messageResource = Res.string.bookmark_removed_successfully,
            status = SnackBarState.Status.Success
        )
    }

    private fun onGetBookmarksSuccess(ayahBookmarksFlow: Flow<PagingData<AyahBookmark>>) {
        updateState {
            it.copy(
                bookmarks = ayahBookmarksFlow
                    .map { pagingData ->
                        pagingData.map { bookmark -> bookmark.toUiState() }
                    }
                    .cachedIn(viewModelScope),
                isLoading = false,
                deletedBookmarkIds = deletedBookmarkIds
            )
        }
    }

    private fun createBookmarksPagingSource(): Flow<PagingData<AyahBookmark>> =
        createPagingSourceFlow { _, pageNumber -> bookmarkRepository.getAyahBookmarks(pageNumber) }

    private fun handleErrorState(throwable: Throwable) {
        // TODO: handle error here
    }

}

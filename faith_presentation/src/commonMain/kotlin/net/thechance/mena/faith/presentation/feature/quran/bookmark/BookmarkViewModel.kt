package net.thechance.mena.faith.presentation.feature.quran.bookmark

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.bookmark_removed_successfully
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.base.createPagingSourceFlow

class BookmarkViewModel(
    private val bookmarkRepository: BookmarkRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<BookMarkUiState, BookmarkEffect>(BookMarkUiState()),
    BookmarkInteractionListener {

    private val cachedBookmarksFlow = createBookmarksPagingSource()
        .map { pagingData -> pagingData.map(AyahBookmark::toUiState) }.cachedIn(viewModelScope)

    private val deletedBookmarkIdsFlow = MutableStateFlow(setOf<Int>())
    private var pendingDeleteBookmarkId: Int? = null

    private val filteredBookmarksFlow = combine(cachedBookmarksFlow, deletedBookmarkIdsFlow)
    { pagingData, deletedIds ->
        pagingData.filter { bookmark -> bookmark.bookmarkId !in deletedIds }
    }

    init {
        initializeBookmarks()
    }

    override fun onBackClick() = sendEffect(BookmarkEffect.NavigateBack)

    override fun onStartTilawahClick() = sendEffect(BookmarkEffect.NavigateToSur)

    override fun onDeleteBookmarkClick(bookmarkId: Int) {
        pendingDeleteBookmarkId = bookmarkId
        updateState { it.copy(isDeleteConfirmationDialogVisible = true) }
    }

    override fun onConfirmDeleteBookmarkClick() {
        val bookmarkId = pendingDeleteBookmarkId ?: return

        tryToExecute(
            dispatcher = dispatcher,
            execute = { bookmarkRepository.deleteAyahBookmark(bookmarkId) },
            onStart = { insertDeletedBookmarkId(bookmarkId) },
            onSuccess = { onDeleteBookmarkSuccess() },
            onError = {
                removeDeletedBookmarkId(bookmarkId)
                handleErrorSnackBar(it)
                onDismissDeleteConfirmationDialog()
            },
        )
    }

    override fun onDismissDeleteConfirmationDialog() {
        updateState { it.copy(isDeleteConfirmationDialogVisible = false) }
        pendingDeleteBookmarkId = null
    }

    private fun insertDeletedBookmarkId(id: Int) =
        deletedBookmarkIdsFlow.update { currentSet -> currentSet + id }

    private fun removeDeletedBookmarkId(id: Int) =
        deletedBookmarkIdsFlow.update { currentSet -> currentSet - id }

    private fun initializeBookmarks() {
        updateState {
            it.copy(
                bookmarks = filteredBookmarksFlow,
                isLoading = false,
            )
        }
    }

    private fun onDeleteBookmarkSuccess() {
        handleSuccessSnackBar(Res.string.bookmark_removed_successfully)
        updateState { it.copy(isDeleteConfirmationDialogVisible = false) }
        pendingDeleteBookmarkId = null
    }

    private fun createBookmarksPagingSource(): Flow<PagingData<AyahBookmark>> =
        createPagingSourceFlow { pageNumber, pageSize ->
            bookmarkRepository.getAyahBookmarks(pageNumber = pageNumber, pageSize = pageSize)
        }

}

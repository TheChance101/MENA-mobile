package net.thechance.mena.faith.presentation.feature.quran.bookmark

import app.cash.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import net.thechance.mena.faith.presentation.extensions.timeFormatingHelper.TimeAgo

data class BookmarksScreenState(
    val bookmarks: Flow<PagingData<BookmarkCardUiState>> = emptyFlow(),
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    data class BookmarkCardUiState(
        val bookmarkId: Int = 0,
        val surahName: String = "",
        val ayaNumber: Int = 0,
        val ayaText: String = "",
        val createdAt: TimeAgo
    )
}

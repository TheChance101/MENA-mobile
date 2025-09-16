package net.thechance.mena.faith.presentation.feature.quran.bookmark

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class BookmarksScreenState(
    val bookmarks: List<BookmarkUiState> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
){
    data class BookmarkUiState (
        val bookmarkId: Int = 0,
        val surahName: String = "",
        val ayaNumber: Int = 0,
        val ayaText: String = "",
        val createdAt: Instant = Clock.System.now(),
    )
}

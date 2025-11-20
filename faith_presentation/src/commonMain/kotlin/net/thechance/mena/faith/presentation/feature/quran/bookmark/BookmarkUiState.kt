package net.thechance.mena.faith.presentation.feature.quran.bookmark

import app.cash.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.calculateTimeAgo
import kotlin.time.ExperimentalTime

data class BookMarkUiState(
    val bookmarks: Flow<PagingData<BookmarkCardUiState>> = emptyFlow(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDeleteConfirmationDialogVisible: Boolean = false,
    ) {
    data class BookmarkCardUiState(
        val bookmarkId: Int = 0,
        val surahName: String = "",
        val ayaNumber: Int = 0,
        val ayaText: String = "",
        val createdAt: TimeAgo
    )
}

data class TimeAgo(
    val amount: Int = 0,
    val unit: TimeUnit = TimeUnit.SECONDS
)

enum class TimeUnit {
    SECONDS,
    MINUTES,
    HOURS,
    DAYS,
    WEEKS,
    MONTHS,
    YEARS
}

@OptIn(ExperimentalTime::class)
fun AyahBookmark.toUiState() = BookMarkUiState.BookmarkCardUiState(
    bookmarkId = id,
    surahName = surah.name,
    ayaNumber = ayah.number,
    ayaText = ayah.content,
    createdAt = createdAt.calculateTimeAgo(),
)


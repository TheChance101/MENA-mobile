package net.thechance.mena.faith.presentation.feature.quran.bookmark

import net.thechance.mena.faith.domain.entity.Bookmark
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Bookmark.toUiState() = BookmarksScreenState.BookmarkCardUiState(
    bookmarkId = id,
    surahName = surah.name,
    ayaNumber = ayah.number,
    ayaText = ayah.content,
    createdAt = createdAt
)

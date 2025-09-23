package net.thechance.mena.faith.presentation.feature.quran.bookmark

import net.thechance.mena.faith.domain.entity.AyahBookmark
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun AyahBookmark.toUiState() = BookmarksScreenState.BookmarkCardUiState(
    bookmarkId = id,
    surahName = surah.name,
    ayaNumber = ayah.number,
    ayaText = ayah.content,
    createdAt = createdAt
)

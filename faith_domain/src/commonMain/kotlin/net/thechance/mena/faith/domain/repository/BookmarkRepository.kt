package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.AyahBookmark

interface BookmarkRepository {
    suspend fun getAllAyahBookmarks(): List<AyahBookmark>
    suspend fun addAyahBookmark(surahId: Int, ayahNumber: Int): AyahBookmark
    suspend fun deleteAyahBookmark(ayahBookmarkId: Int)
}

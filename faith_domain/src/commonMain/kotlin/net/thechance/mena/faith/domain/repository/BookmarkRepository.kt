package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.AyahBookmark

interface BookmarkRepository {
    suspend fun getAyahBookmarks(pageNumber: Int, pageSize: Int): List<AyahBookmark>
    suspend fun addAyahBookmark(surahId: Int, ayahNumber: Int)
    suspend fun deleteAyahBookmark(ayahBookmarkId: Int)
}

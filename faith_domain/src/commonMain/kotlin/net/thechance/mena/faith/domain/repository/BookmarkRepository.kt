package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.Bookmark

interface BookmarkRepository {
    suspend fun getBookmarks(): List<Bookmark>
    suspend fun addBookmark(surahId: Int, ayahNumber: Int): Bookmark
    suspend fun deleteBookmark(bookmarkId: Int)
}
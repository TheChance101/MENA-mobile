package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.Bookmark

interface BookmarkRepository {
    suspend fun getAllBookmarks(): List<Bookmark>
    suspend fun addBookmark(surahId: Int, ayahNumber: Int): Bookmark
    suspend fun removeBookmark(bookmarkId: Int)
}

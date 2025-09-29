package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.entity.PagedFetchResponse

interface BookmarkRepository {
    suspend fun getAyahBookmarks(pageNumber: Int): PagedFetchResponse<AyahBookmark>
    suspend fun addAyahBookmark(surahId: Int, ayahNumber: Int): AyahBookmark
    suspend fun deleteAyahBookmark(ayahBookmarkId: Int)
}

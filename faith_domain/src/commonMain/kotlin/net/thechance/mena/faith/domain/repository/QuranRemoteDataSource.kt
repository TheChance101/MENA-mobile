package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.Bookmark

interface QuranRemoteDataSource {
    fun getBookmarks(): List<Bookmark>
    fun saveAyahBookmark(ayahNo: Int, surahId:Int): Bookmark
    fun deleteBookmark(id: Int)
}
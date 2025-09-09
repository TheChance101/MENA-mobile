package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Bookmark
import net.thechance.mena.faith.domain.entity.Surah

interface QuranRepository {
    fun getSur(): List<Surah>
    fun getSurahById(id: Int): Surah
    fun getAyatOfSurah(surahId: Int): List<Ayah>
    fun getAyatContent(ayahNo: Int, surahId: Int): String
    fun getBookmark(): List<Bookmark>
    fun saveAyahBookmark(ayahNo: Int, surahId: Int): Bookmark
    fun deleteBookmark(id: Int)
}
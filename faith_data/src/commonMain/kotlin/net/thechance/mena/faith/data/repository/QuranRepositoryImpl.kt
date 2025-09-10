package net.thechance.mena.faith.data.repository

import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Bookmark
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.QuranRepository

class QuranRepositoryImpl(
    // Inject Quran and Bookmark Impl classes
): QuranRepository {
    override fun getSur(): List<Surah> {
        TODO("Not yet implemented")
    }

    override fun getSurahById(id: Int): Surah {
        TODO("Not yet implemented")
    }

    override fun getAyatOfSurah(surahId: Int): List<Ayah> {
        TODO("Not yet implemented")
    }

    override fun getAyatContent(ayahNo: Int, surahId: Int): String {
        TODO("Not yet implemented")
    }

    override fun getBookmark(): List<Bookmark> {
        TODO("Not yet implemented")
    }

    override fun saveAyahBookmark(
        ayahNo: Int,
        surahId: Int
    ): Bookmark {
        TODO("Not yet implemented")
    }

    override fun deleteBookmark(id: Int) {
        TODO("Not yet implemented")
    }
}
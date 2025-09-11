package net.thechance.mena.faith.data.repository

import net.thechance.mena.faith.domain.entity.Ayah
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
}
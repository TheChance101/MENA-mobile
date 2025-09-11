package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah

interface QuranRepository {
    fun getSur(): List<Surah>
    fun getSurahById(id: Int): Surah
    fun getAyatOfSurah(surahId: Int): List<Ayah>
    fun getAyatContent(ayahNo: Int, surahId: Int): String
}
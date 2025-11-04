package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.model.LastAyahForTilawah

interface QuranRepository {
    suspend fun getSur(): List<Surah>
    suspend fun getAyatOfSurah(surahId: Int): List<Ayah>
    suspend fun getLastAyahForTilawah(): LastAyahForTilawah
    suspend fun saveLastAyahForTilawah(savedAyah: LastAyahForTilawah)
    suspend fun searchForAyahInSurah(surahId: Int, query: String): List<Ayah>
    suspend fun searchForAyahInQuran(query: String): List<Ayah>
    suspend fun getAyahSoundUrl(ayahNumber: Int, surahNumber: Int, reciterId: Int): String
}

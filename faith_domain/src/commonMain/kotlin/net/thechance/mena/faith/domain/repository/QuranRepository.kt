package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah

interface QuranRepository {
    suspend fun getAllSur(): List<Surah>
    suspend fun getAyatOfSurah(surahId: Int): List<Ayah>
    suspend fun getLastAyahForTilawah(): Ayah
    suspend fun saveLastAyahForTilawah(ayah: Ayah)
    suspend fun searchForAyahInSurah(surahId: Int, query: String): List<Ayah>
    suspend fun searchForAyahInQuran(query: String): List<Ayah>
}

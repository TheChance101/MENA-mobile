package net.thechance.mena.faith.domain.repository

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.model.DownlodedSur
import net.thechance.mena.faith.domain.model.LastAyahForTilawah
import net.thechance.mena.faith.domain.model.Reciter

interface QuranRepository {
    suspend fun getSur(): List<Surah>
    suspend fun getAyatOfSurah(surahId: Int): List<Ayah>
    suspend fun getLastAyahForTilawah(): LastAyahForTilawah
    suspend fun saveLastAyahForTilawah(savedAyah: LastAyahForTilawah)
    suspend fun getDownloadedSur(): List<DownlodedSur>
    suspend fun searchForAyahInSurah(surahId: Int, query: String): List<Ayah>
    suspend fun searchForAyahInQuran(query: String): List<Ayah>
    suspend fun searchForReciter(query: String): List<Reciter>
    suspend fun getAyahSoundUrl(ayahNumber: Int, surahNumber: Int, reciterId: Int): String
    suspend fun isSurahAudioCached(surahId: Int, reciterId: Int): Boolean
    suspend fun getSurahById(surahId: Int): Surah
    suspend fun getSurahAudioCachePath(surahId: Int, reciterId: Int): String?
    suspend fun saveSurahAudioToCache(surahId: Int, reciterId: Int, localPath: String)
    suspend fun deleteSurahWithSpecificReciter(surahId: Int)
    suspend fun getRemoteSurahSoundUrl(surahId: Int, reciterId: Int): String
    suspend fun getReciters(): List<Reciter>
    suspend fun getReciterById(reciterId: Int): Reciter
    suspend fun saveDefaultReciter(reciterId: Int)
    suspend fun getDefaultReciter(): Flow<Int>
    suspend fun deleteSurahAudioByReciter(surahId: Int, reciterId: Int)

}
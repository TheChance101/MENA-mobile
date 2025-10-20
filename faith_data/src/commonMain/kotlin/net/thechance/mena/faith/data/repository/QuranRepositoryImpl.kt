package net.thechance.mena.faith.data.repository

import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.database.AyahDto
import net.thechance.mena.faith.data.datastore.TilawahDataStore
import net.thechance.mena.faith.data.mapper.toAyah
import net.thechance.mena.faith.data.mapper.toSurah
import net.thechance.mena.faith.data.utils.executeLocalSafely
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.model.LastAyahForTilawah
import net.thechance.mena.faith.domain.repository.QuranRepository

class QuranRepositoryImpl(
    val ayahDao: AyahDao,
    val tilawahDataStore: TilawahDataStore
) : QuranRepository {

    override suspend fun getAllSur(): List<Surah> =
        executeLocalSafely {
            ayahDao.getAllSur().map { it.toSurah() }
        }

    override suspend fun getAyatOfSurah(surahId: Int): List<Ayah> =
        executeLocalSafely {
            ayahDao.getAyatOfSurah(surahId).map { it.toAyah() }
        }

    override suspend fun getLastAyahForTilawah(): LastAyahForTilawah {
        return tilawahDataStore.getLastAyah()
            ?: LastAyahForTilawah(number = 1, surahId = 1, surahName = "Al-Fatiha")
    }

    override suspend fun saveLastAyahForTilawah(savedAyah: LastAyahForTilawah) =
        tilawahDataStore.saveLastAyah(savedAyah)

    override suspend fun searchForAyahInSurah(
        surahId: Int,
        query: String,
    ): List<Ayah> =
        executeLocalSafely {
            ayahDao.searchForAyahInSurah(surahId = surahId, query = query).map(AyahDto::toAyah)
        }

    override suspend fun searchForAyahInQuran(query: String): List<Ayah> =
        executeLocalSafely {
            ayahDao.searchForAyahInQuran(query).map(AyahDto::toAyah)
        }
}

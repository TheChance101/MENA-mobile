package net.thechance.mena.faith.data.repository

import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.mapper.toAyah
import net.thechance.mena.faith.data.mapper.toSurah
import net.thechance.mena.faith.data.utils.executeLocalSafely
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.QuranRepository

class QuranRepositoryImpl(val ayahDao: AyahDao) : QuranRepository {

    override suspend fun getAllSur(): List<Surah> =
        executeLocalSafely {
            ayahDao.getAllSur().map { it.toSurah() }
        }

    override suspend fun getAyatOfSurah(surahId: Int): List<Ayah> =
        executeLocalSafely {
            ayahDao.getAyatOfSurah(surahId).map { it.toAyah() }
        }

    override suspend fun searchForAyahInSurah(
        surahId: Int,
        query: String
    ): List<Ayah> {
        //Not yet implemented
        return emptyList()
    }

    override suspend fun searchForAyahInQuran(query: String): List<Ayah> {
        //Not yet implemented
        return emptyList()
    }

    override suspend fun getLastAyahForTilawah(): Ayah {
        //Not yet implemented
        return Ayah(number = 1, surahId = 1, content = "", plainContent = "")
    }

    override suspend fun saveLastAyahForTilawah(ayah: Ayah) {
        //Not yet implemented
    }
}

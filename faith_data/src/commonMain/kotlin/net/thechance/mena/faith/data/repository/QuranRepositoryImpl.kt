package net.thechance.mena.faith.data.repository

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.database.AyahDto
import net.thechance.mena.faith.data.datastore.TilawahDataStore
import net.thechance.mena.faith.data.mapper.toAyah
import net.thechance.mena.faith.data.mapper.toReciter
import net.thechance.mena.faith.data.mapper.toReciterDto
import net.thechance.mena.faith.data.mapper.toSurah
import net.thechance.mena.faith.data.remote.model.tilawah.AyahSoundUrlRequest
import net.thechance.mena.faith.data.remote.service.TilawahApiService
import net.thechance.mena.faith.data.utils.executeApiSafely
import net.thechance.mena.faith.data.utils.executeLocalSafely
import net.thechance.mena.faith.data.utils.loadFromCacheOrFetch
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.model.LastAyahForTilawah
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository

class QuranRepositoryImpl(
    val ayahDao: AyahDao,
    val tilawahApiService: TilawahApiService,
    val tilawahDataStore: TilawahDataStore
) : QuranRepository {

    override suspend fun getSur(): List<Surah> =
        executeLocalSafely {
            ayahDao.getSur().map { it.toSurah() }
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

    override suspend fun searchForReciter(query: String): List<Reciter> =
        executeLocalSafely { ayahDao.searchReciters(query).map { it.toReciter() } }

    override suspend fun getAyahSoundUrl(
        ayahNumber: Int,
        surahNumber: Int,
        reciterId: Int
    ): String = executeApiSafely<String> {
        val requestBody = AyahSoundUrlRequest(
            ayahNumber = ayahNumber,
            surahNumber = surahNumber,
            reciterId = reciterId
        )
        tilawahApiService.getAyahSoundUrl(requestBody)
    }

    override suspend fun getReciters(): List<Reciter> = loadFromCacheOrFetch(
        cacheBlock = {
            executeLocalSafely { ayahDao.getAllReciters() }.takeIf { it.isNotEmpty() }
                ?.map { it.toReciter() }
        },
        networkBlock = { executeApiSafely { tilawahApiService.getReciters() }.map { it.toReciter() } },
        syncBlock = { reciters ->
            executeLocalSafely { ayahDao.insertReciters(reciters.map { it.toReciterDto() }) }
        }
    )

    override suspend fun getReciterById(reciterId: Int): Reciter = loadFromCacheOrFetch(
        cacheBlock = { executeLocalSafely { ayahDao.getReciterById(reciterId) }.toReciter() },
        networkBlock = {
            executeApiSafely { tilawahApiService.getReciters() }
                .first { it.id == reciterId }
                .toReciter()
        }
    )

    override suspend fun saveDefaultReciter(reciterId: Int) =
        tilawahDataStore.saveDefaultReciter(reciterId)

    override suspend fun getDefaultReciter(): Flow<Int> =
        tilawahDataStore.getDefaultReciter()
}

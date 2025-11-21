package net.thechance.mena.faith.data.repository

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.database.AyahDto
import net.thechance.mena.faith.data.database.RecitersDao
import net.thechance.mena.faith.data.database.SurahAudioDao
import net.thechance.mena.faith.data.database.SurahAudioDto
import net.thechance.mena.faith.data.datastore.TilawahDataStore
import net.thechance.mena.faith.data.mapper.toAyah
import net.thechance.mena.faith.data.mapper.toDownlodedSurUi
import net.thechance.mena.faith.data.mapper.toReciter
import net.thechance.mena.faith.data.mapper.toReciterDto
import net.thechance.mena.faith.data.mapper.toSurah
import net.thechance.mena.faith.data.remote.service.TilawahApiService
import net.thechance.mena.faith.data.utils.executeApiSafely
import net.thechance.mena.faith.data.utils.executeLocalSafely
import net.thechance.mena.faith.data.utils.loadFromCacheOrFetch
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.model.DownlodedSur
import net.thechance.mena.faith.domain.model.LastAyahForTilawah
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.identity.domain.service.LocalizationService
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM

class QuranRepositoryImpl(
    private val ayahDao: AyahDao,
    private val surahSoundDao: SurahAudioDao,
    private val recitersDao: RecitersDao,
    private val tilawahApiService: TilawahApiService,
    private val tilawahDataStore: TilawahDataStore,
    private val localizationService: LocalizationService,
) : QuranRepository {
    override suspend fun getSur(): List<Surah> =
        executeLocalSafely {
            ayahDao.getSur().map { it.toSurah(localizationService.getCurrentLanguage()) }
        }

    override suspend fun getAyatOfSurah(surahId: Int): List<Ayah> =
        executeLocalSafely {
            ayahDao.getAyatOfSurah(surahId).map { it.toAyah() }
        }

    override suspend fun getLastAyahForTilawah(): LastAyahForTilawah {
        return tilawahDataStore.getLastAyah()
            ?: LastAyahForTilawah(number = 1, surahId = 1)
    }

    override suspend fun saveLastAyahForTilawah(savedAyah: LastAyahForTilawah) =
        tilawahDataStore.saveLastAyah(savedAyah)

    override suspend fun getDownloadedSur(): List<DownlodedSur> = executeLocalSafely {
        surahSoundDao.getDownloadedSurahInfo()
            .groupBy { it.surahId }
            .map { (surahId, items) -> mapToDownloadedSur(surahId, items) }
    }

    private suspend fun mapToDownloadedSur(
        surahId: Int,
        items: List<SurahAudioDto>
    ): DownlodedSur {
        return items.first().toDownlodedSurUi(
            surahName = getSurahById(surahId).name,
            reciterName = getRecitersNames(items)
        )
    }

    private suspend fun getRecitersNames(items: List<SurahAudioDto>): List<String> =
        items.map { recitersDao.getReciterById(it.reciterId).name }

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

    override suspend fun getSurahAudioCachePath(surahId: Int, reciterId: Int): String? =
        executeLocalSafely {
            surahSoundDao.getCachedAudioPath(surahId, reciterId)?.takeIf { path ->
                FileSystem.SYSTEM.exists(path.toPath())
            }
        }

    override suspend fun saveSurahAudioToCache(
        surahId: Int,
        reciterId: Int,
        localPath: String
    ) {
        executeLocalSafely {
            val surahSound = SurahAudioDto(
                surahId = surahId,
                reciterId = reciterId,
                localFilePath = localPath
            )
            surahSoundDao.saveSurahAudio(surahSound)
        }
    }

    override suspend fun deleteSurahWithSpecificReciter(surahId: Int) {
        recitersDao.deleteSurahAudioByReciter(surahId)
    }

    override suspend fun getRemoteSurahSoundUrl(
        surahId: Int,
        reciterId: Int
    ): String = executeApiSafely {
        tilawahApiService.getSurahSoundUrl(
            reciterId = reciterId,
            surahNumber = surahId,
        )
    }

    override suspend fun isSurahAudioCached(surahId: Int, reciterId: Int): Boolean =
        executeLocalSafely {
            getSurahAudioCachePath(surahId, reciterId) != null
        }

    override suspend fun getSurahById(surahId: Int): Surah =
        executeLocalSafely {
            ayahDao.getSurah(surahId).toSurah(localizationService.getCurrentLanguage())
        }

    override suspend fun searchForReciter(query: String): List<Reciter> =
        executeLocalSafely { recitersDao.searchReciters(query).map { it.toReciter() } }

    override suspend fun getAyahSoundUrl(
        ayahNumber: Int,
        surahNumber: Int,
        reciterId: Int
    ): String {

        val surahSound = getSurahAudioCachePath(surahNumber, reciterId)

        if (!surahSound.isNullOrEmpty()) findAyahInFolder(
            surahSound,
            ayahNumber,
            surahNumber
        )?.let {
            return it
        }

        return executeApiSafely<String> {
            tilawahApiService.getAyahSoundUrl(
                reciterId = reciterId,
                ayahNumber = ayahNumber,
                surahNumber = surahNumber,
            )
        }
    }

    private suspend fun findAyahInFolder(
        folderPath: String,
        ayahNumber: Int,
        surahNumber: Int
    ): String? {
        val folder = folderPath.toPath()

        if (!FileSystem.SYSTEM.exists(folder)) return null

        val files = FileSystem.SYSTEM.list(folder)
        val ayatCount = ayahDao.getSurah(surahNumber).ayahCount ?: 0


        val preparedFiles = prepareSurahAudioFiles(
            files = files,
            ayatCount = ayatCount
        )

        val fileIndex = calculateFileIndex(ayahNumber, surahNumber)
        return preparedFiles.getOrNull(fileIndex)?.toString()
    }


    private fun prepareSurahAudioFiles(
        files: List<Path>,
        ayatCount: Int
    ): List<Path> {
        return if (files.size > ayatCount)
            files.subList(
                INDEX_OFFSET,
                files.size - ayatCount
            ) else files
    }

    override suspend fun deleteSurahAudioByReciter(surahId: Int, reciterId: Int) {
        executeLocalSafely {
            recitersDao.deleteSpecificDownloadedAudio(surahId = surahId,reciterId = reciterId)
        }
    }

    private fun calculateFileIndex(ayahNumber: Int, surahNumber: Int): Int {
        return if (surahNumber == INDEX_OFFSET) ayahNumber
        else ayahNumber + INDEX_OFFSET
    }

    override suspend fun getReciters(): List<Reciter> = loadFromCacheOrFetch(
        cacheBlock = {
            executeLocalSafely { recitersDao.getAllReciters() }.takeIf { it.isNotEmpty() }
                ?.map { it.toReciter() }
        },
        networkBlock = { executeApiSafely { tilawahApiService.getReciters() }.map { it.toReciter() } },
        syncBlock = { reciters ->
            executeLocalSafely { recitersDao.insertReciters(reciters.map { it.toReciterDto() }) }
        }
    )

    override suspend fun getReciterById(reciterId: Int): Reciter = loadFromCacheOrFetch(
        cacheBlock = { executeLocalSafely { recitersDao.getReciterById(reciterId) }.toReciter() },
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

private const val INDEX_OFFSET = 1





package net.thechance.mena.faith.data.repository

import de.jensklingenberg.ktorfit.Response
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.database.ReciterDto
import net.thechance.mena.faith.data.database.RecitersDao
import net.thechance.mena.faith.data.database.SurahAudioDao
import net.thechance.mena.faith.data.database.SurahAudioDto
import net.thechance.mena.faith.data.database.SurahDto
import net.thechance.mena.faith.data.datastore.TilawahDataStore
import net.thechance.mena.faith.data.remote.model.tilawah.RecitersRequest
import net.thechance.mena.faith.data.remote.service.TilawahApiService
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.model.LastAyahForTilawah
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.identity.domain.service.LocalizationService
import net.thechance.mena.identity.domain.util.AppLanguage
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class QuranRepositoryImplTest {
    private val mockDao: AyahDao = mock(MockMode.autofill)
    private val tilawahDataStore: TilawahDataStore = mock(MockMode.autofill)
    private val surahSoundDao: SurahAudioDao = mock(MockMode.autofill)
    private val recitersDao: RecitersDao = mock(MockMode.autofill)
    private val tilawahApiService = mock<TilawahApiService>(MockMode.autofill)
    private val settingsRepository: SettingsRepository = mock(MockMode.autofill)
    private lateinit var repository: QuranRepository

    @BeforeTest
    fun setup() {
        repository = QuranRepositoryImpl(
            ayahDao = mockDao,
            tilawahDataStore = tilawahDataStore,
            surahSoundDao = surahSoundDao,
            recitersDao = recitersDao,
            tilawahApiService = tilawahApiService,
            localizationService = LocalizationService(settingsRepository)
        )
    }

    @Test
    fun `getSur Should return list of sur when called`() = runTest {
        everySuspend { mockDao.getSur() } returns SURAH_DTOS
        every { settingsRepository.getCurrentAppLanguage() } returns AppLanguage.ENGLISH

        val result = repository.getSur()

        assertEquals(SUR_LIST, result)
    }

    @Test
    fun `searchForReciter Should return list of reciters when called`() = runTest {

        everySuspend { recitersDao.searchReciters(query = "query") } returns RECITER_DTOS

        val result = repository.searchForReciter(query = "query")

        assertEquals(RECITER_LIST, result)
    }

    @Test
    fun `getSur Should return empty list when database is empty`() = runTest {
        everySuspend { mockDao.getSur() } returns emptyList()

        val result = repository.getSur()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAyatOfSurah Should return empty list when surah has no ayat`() = runTest {
        everySuspend { mockDao.getAyatOfSurah(1) } returns emptyList()

        val result = repository.getAyatOfSurah(1)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAyatOfSurah Should return empty list when surah id is non exist`() = runTest {
        everySuspend { mockDao.getAyatOfSurah(999) } returns emptyList()

        val result = repository.getAyatOfSurah(999)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAyatOfSurah Should return empty list when surah id is zero`() = runTest {
        everySuspend { mockDao.getAyatOfSurah(0) } returns emptyList()

        val result = repository.getAyatOfSurah(0)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `searchForAyahInQuran Should return empty list when no ayah match the query`() = runTest {
        everySuspend { mockDao.searchForAyahInQuran("nonexistent") } returns emptyList()

        val result = repository.searchForAyahInQuran("nonexistent")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `searchForAyahInSurah Should return empty list when no ayah match the query in the specified surah`() =
        runTest {
            everySuspend { mockDao.searchForAyahInSurah(1, "nonexistent") } returns emptyList()

            val result = repository.searchForAyahInSurah(1, "nonexistent")

            assertTrue(result.isEmpty())
        }

    @Test
    fun `getLastAyahForTilawah should return stored ayah when datastore has value`() = runTest {
        everySuspend { tilawahDataStore.getLastAyah() } returns SAVED_TILAWAH_PROGRESS

        val result = repository.getLastAyahForTilawah()

        assertEquals(SAVED_TILAWAH_PROGRESS, result)
    }

    @Test
    fun `getLastAyahForTilawah should return default ayah when datastore is empty`() = runTest {
        everySuspend { tilawahDataStore.getLastAyah() } returns null

        val result = repository.getLastAyahForTilawah()

        assertEquals(DEFAULT_TILAWAH, result)
    }

    @Test
    fun `saveLastAyahForTilawah should call datastore saveLastAyah`() = runTest {
        val ayahToSave = TILAWAH_AYAH_TO_SAVE

        repository.saveLastAyahForTilawah(ayahToSave)

        verifySuspend {
            tilawahDataStore.saveLastAyah(ayahToSave)
        }
    }

    @Test
    fun `saveDefaultReciter should call datastore saveDefaultReciter`() = runTest {
        val reciterId = 1

        repository.saveDefaultReciter(reciterId)

        verifySuspend {
            tilawahDataStore.saveDefaultReciter(reciterId)
        }
    }

    @Test
    fun `getDefaultReciter should call datastore getDefaultReciter`() = runTest {
        val expectedReciterId = 1
        everySuspend { tilawahDataStore.getDefaultReciter() } returns flowOf(expectedReciterId)
        val result = repository.getDefaultReciter()
        assertEquals(expectedReciterId, result.first())
    }

    private fun <T> makeSuccessFakeResponse(
        body: T,
        successStatus: HttpStatusCode = HttpStatusCode.OK
    ): Response<T> {
        val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
            everySuspend { status } returns successStatus
        }
        return Response.success(
            body = body,
            rawResponse = mockHttpResponse
        ) as Response<T>
    }

    @Test
    fun `getReciters should return mapped reciters`() = runTest {
        val apiReciters = listOf(
            RecitersRequest(
                id = 1,
                name = FIST_RECITER_NAME,
                arabicName = FIST_RECITER_ARABIC_NAME,
                tilawahType = FIRST_TILWAH_TYPE
            ),
            RecitersRequest(
                id = 2,
                name = SECOND_RECITER_NAME,
                arabicName = SECOND_RECITER_ARABIC_NAME,
                tilawahType = SECOND_TILWAH_TYPE
            )
        )

        everySuspend {
            tilawahApiService.getReciters()
        } returns makeSuccessFakeResponse(apiReciters)

        val result = repository.getReciters()

        assertEquals(apiReciters.map { it.id }, result.map { it.id })
        assertEquals(apiReciters.map { it.name }, result.map { it.name })
        verifySuspend { tilawahApiService.getReciters() }
    }

    @Test
    fun `getReciters should return empty list when api returns empty`() = runTest {
        everySuspend {
            tilawahApiService.getReciters()
        } returns makeSuccessFakeResponse(emptyList())

        val result = repository.getReciters()

        assertTrue(result.isEmpty())
        verifySuspend { tilawahApiService.getReciters() }
    }

    @Test
    fun `getSurahAudioCachePath should return null when path not found in database`() = runTest {
        everySuspend { surahSoundDao.getCachedAudioPath(SURAH_ID_1, RECITER_ID_1) } returns null

        val result = repository.getSurahAudioCachePath(SURAH_ID_1, RECITER_ID_1)

        assertEquals(null, result)
    }

    @Test
    fun `getSurahAudioCachePath should return null when file does not exist on filesystem`() =
        runTest {
            everySuspend {
                surahSoundDao.getCachedAudioPath(
                    SURAH_ID_1,
                    RECITER_ID_1
                )
            } returns NON_EXISTENT_PATH
            // Mock FileSystem.SYSTEM.exists to return false

            val result = repository.getSurahAudioCachePath(SURAH_ID_1, RECITER_ID_1)

            assertEquals(null, result)
        }

    @Test
    fun `saveSurahAudioToCache should call dao saveSurahAudio with correct parameters`() = runTest {
        repository.saveSurahAudioToCache(SURAH_ID_1, RECITER_ID_1, LOCAL_PATH_SURAH_1)

        verifySuspend {
            surahSoundDao.saveSurahAudio(
                SurahAudioDto(
                    surahId = SURAH_ID_1,
                    reciterId = RECITER_ID_1,
                    localFilePath = LOCAL_PATH_SURAH_1
                )
            )
        }
    }

    @Test
    fun `saveSurahAudioToCache should save multiple different surahs`() = runTest {
        repository.saveSurahAudioToCache(SURAH_ID_1, RECITER_ID_1, LOCAL_PATH_SURAH_1_SIMPLE)
        repository.saveSurahAudioToCache(SURAH_ID_2, RECITER_ID_1, LOCAL_PATH_SURAH_2_SIMPLE)

        verifySuspend {
            surahSoundDao.saveSurahAudio(
                SurahAudioDto(
                    surahId = SURAH_ID_1,
                    reciterId = RECITER_ID_1,
                    localFilePath = LOCAL_PATH_SURAH_1_SIMPLE
                )
            )
        }
        verifySuspend {
            surahSoundDao.saveSurahAudio(
                SurahAudioDto(
                    surahId = SURAH_ID_2,
                    reciterId = RECITER_ID_1,
                    localFilePath = LOCAL_PATH_SURAH_2_SIMPLE
                )
            )
        }
    }

    @Test
    fun `getRemoteSurahSoundUrl should return url from api service`() = runTest {
        everySuspend {
            tilawahApiService.getSurahSoundUrl(
                reciterId = RECITER_ID_1, surahNumber = SURAH_ID_1
            )
        } returns makeSuccessFakeResponse(REMOTE_URL_SURAH_1_RECITER_1)

        val result = repository.getRemoteSurahSoundUrl(SURAH_ID_1, RECITER_ID_1)

        assertEquals(REMOTE_URL_SURAH_1_RECITER_1, result)
        verifySuspend {
            tilawahApiService.getSurahSoundUrl(
                RECITER_ID_1,
                SURAH_ID_1
            )
        }
    }

    @Test
    fun `getRemoteSurahSoundUrl should call api with different surah and reciter ids`() = runTest {
        everySuspend {
            tilawahApiService.getSurahSoundUrl(RECITER_ID_1, SURAH_ID_1)
        } returns makeSuccessFakeResponse(REMOTE_URL_SURAH_1)

        everySuspend {
            tilawahApiService.getSurahSoundUrl(RECITER_ID_2, SURAH_ID_2)
        } returns makeSuccessFakeResponse(REMOTE_URL_SURAH_2)

        val result1 = repository.getRemoteSurahSoundUrl(SURAH_ID_1, RECITER_ID_1)
        val result2 = repository.getRemoteSurahSoundUrl(SURAH_ID_2, RECITER_ID_2)

        assertEquals(REMOTE_URL_SURAH_1, result1)
        assertEquals(REMOTE_URL_SURAH_2, result2)
    }

    @Test
    fun `isSurahAudioCached should return false when audio is not cached`() = runTest {
        everySuspend { surahSoundDao.getCachedAudioPath(SURAH_ID_1, RECITER_ID_1) } returns null

        val result = repository.isSurahAudioCached(SURAH_ID_1, RECITER_ID_1)

        assertEquals(false, result)
    }

    @Test
    fun `isSurahAudioCached should return false when cached file does not exist`() = runTest {
        everySuspend {
            surahSoundDao.getCachedAudioPath(
                SURAH_ID_1,
                RECITER_ID_1
            )
        } returns NON_EXISTENT_PATH

        val result = repository.isSurahAudioCached(SURAH_ID_1, RECITER_ID_1)

        assertEquals(false, result)
    }

    @Test
    fun `getAyahSoundUrl should return remote url when surah is not cached`() = runTest {
        everySuspend { surahSoundDao.getCachedAudioPath(SURAH_ID_1, RECITER_ID_1) } returns null
        everySuspend {
            tilawahApiService.getAyahSoundUrl(RECITER_ID_1, AYAH_NUMBER_1, SURAH_ID_1)
        } returns makeSuccessFakeResponse(REMOTE_URL_SURAH_1)

        val result = repository.getAyahSoundUrl(
            ayahNumber = AYAH_NUMBER_1,
            surahNumber = SURAH_ID_1,
            reciterId = RECITER_ID_1
        )

        assertEquals(REMOTE_URL_SURAH_1, result)
    }


    @Test
    fun `getAyahSoundUrl should fallback to remote when cached path is empty`() = runTest {
        everySuspend {
            surahSoundDao.getCachedAudioPath(SURAH_ID_1, RECITER_ID_1)
        } returns EMPTY_PATH

        everySuspend {
            tilawahApiService.getAyahSoundUrl(
                RECITER_ID_1, AYAH_NUMBER_1, SURAH_ID_1
            )
        } returns makeSuccessFakeResponse(REMOTE_URL_SURAH_1)

        val result = repository.getAyahSoundUrl(
            ayahNumber = AYAH_NUMBER_1,
            surahNumber = SURAH_ID_1,
            reciterId = RECITER_ID_1
        )

        assertEquals(REMOTE_URL_SURAH_1, result)
    }

    @Test
    fun `getAyahSoundUrl should attempt to find ayah in folder when surah is cached`() = runTest {
        everySuspend {
            surahSoundDao.getCachedAudioPath(SURAH_ID_1, RECITER_ID_1)
        } returns CACHED_SURAH_PATH

        everySuspend {
            mockDao.getSurah(SURAH_ID_1)
        } returns SurahDto(
            number = SURAH_ID_1,
            nameEn = AL_FATIHAH_NAME_EN,
            nameAr = AL_FATIHAH_NAME_AR,
            ayahCount = 7
        )

        everySuspend {
            tilawahApiService.getAyahSoundUrl(
                    reciterId = RECITER_ID_1,
                    ayahNumber = AYAH_NUMBER_5,
                    surahNumber = SURAH_ID_1
            )
        } returns makeSuccessFakeResponse(REMOTE_URL_SURAH_1)

        val result = repository.getAyahSoundUrl(
            ayahNumber = AYAH_NUMBER_5,
            surahNumber = SURAH_ID_1,
            reciterId = RECITER_ID_1
        )

        assertEquals(REMOTE_URL_SURAH_1, result)
    }

    @Test
    fun `getReciters Should return cached reciters when cache is not empty`() = runTest {
        everySuspend { recitersDao.getAllReciters() } returns RECITER_DTOS

        val result = repository.getReciters()

        assertEquals(RECITER_LIST, result)
        verifySuspend { recitersDao.getAllReciters() }
    }

    @Test
    fun `getReciters Should fetch from network when cache is empty`() = runTest {
        everySuspend { recitersDao.getAllReciters() } returns emptyList()
        everySuspend {
            tilawahApiService.getReciters()
        } returns makeSuccessFakeResponse(apiReciters)
        everySuspend { recitersDao.insertReciters(any()) } returns Unit

        val result = repository.getReciters()

        assertEquals(RECITER_LIST, result)
        verifySuspend { recitersDao.getAllReciters() }
        verifySuspend { tilawahApiService.getReciters() }
        verifySuspend { recitersDao.insertReciters(any()) }
    }

    @Test
    fun `getReciters Should sync network data to cache when cache is empty`() = runTest {
        everySuspend { recitersDao.getAllReciters() } returns emptyList()
        everySuspend {
            tilawahApiService.getReciters()
        } returns makeSuccessFakeResponse(apiReciters)
        everySuspend { recitersDao.insertReciters(any()) } returns Unit

        repository.getReciters()

        verifySuspend { recitersDao.insertReciters(any()) }
    }

    @Test
    fun `getReciters Should return empty list when both cache and network are empty`() = runTest {
        everySuspend { mockDao.getAllReciters() } returns emptyList()
        everySuspend {
            tilawahApiService.getReciters()
        } returns makeSuccessFakeResponse(emptyList())

        val result = repository.getReciters()

        assertTrue(result.isEmpty())
    }


    @Test
    fun `findAyahInFolder should return null when ayat count is null`() = runTest {
        everySuspend {
            surahSoundDao.getCachedAudioPath(SURAH_ID_1, RECITER_ID_1)
        } returns CACHED_SURAH_PATH

        everySuspend {
            mockDao.getSurah(SURAH_ID_1)
        } returns SurahDto(
            number = SURAH_ID_1,
            nameEn = AL_FATIHAH_NAME_EN,
            nameAr = AL_FATIHAH_NAME_AR,
            ayahCount = null
        )

        everySuspend {
            tilawahApiService.getAyahSoundUrl(
                RECITER_ID_1, AYAH_NUMBER_1, SURAH_ID_1
            )
        } returns makeSuccessFakeResponse(REMOTE_URL_SURAH_1)

        val result = repository.getAyahSoundUrl(
            ayahNumber = AYAH_NUMBER_1,
            surahNumber = SURAH_ID_1,
            reciterId = RECITER_ID_1
        )

        assertEquals(REMOTE_URL_SURAH_1, result)
    }

    @Test
    fun `findAyahInFolder should return null when file index is out of bounds`() = runTest {
        everySuspend {
            surahSoundDao.getCachedAudioPath(SURAH_ID_1, RECITER_ID_1)
        } returns CACHED_SURAH_PATH

        everySuspend {
            mockDao.getSurah(SURAH_ID_1)
        } returns SurahDto(
            number = SURAH_ID_1,
            nameEn = AL_FATIHAH_NAME_EN, nameAr = AL_FATIHAH_NAME_AR,
            ayahCount = AYAH_COUNT_AL_FATIHAH
        )

        everySuspend {
            tilawahApiService.getAyahSoundUrl(
                RECITER_ID_1, AYAH_NUMBER_OUT_OF_BOUNDS, SURAH_ID_1
            )
        } returns makeSuccessFakeResponse(REMOTE_URL_SURAH_1)

        val result = repository.getAyahSoundUrl(
            ayahNumber = AYAH_NUMBER_OUT_OF_BOUNDS,
            surahNumber = SURAH_ID_1,
            reciterId = RECITER_ID_1
        )

        assertEquals(REMOTE_URL_SURAH_1, result)
    }

    @Test
    fun `deleteSurahWithSpecificReciter should call dao with correct surah id`() = runTest {
        repository.deleteSurahWithSpecificReciter(SURAH_ID_1)

        verifySuspend {
            recitersDao.deleteSurahAudioByReciter(SURAH_ID_1)
        }
    }

    @Test
    fun `deleteSurahWithSpecificReciter should handle multiple deletions`() = runTest {
        repository.deleteSurahWithSpecificReciter(SURAH_ID_1)
        repository.deleteSurahWithSpecificReciter(SURAH_ID_2)

        verifySuspend {
            recitersDao.deleteSurahAudioByReciter(SURAH_ID_1)
        }
        verifySuspend {
            recitersDao.deleteSurahAudioByReciter(SURAH_ID_2)
        }
    }

    @Test
    fun `getReciterById should return reciter from cache when available`() = runTest {
        val expectedReciter = ReciterDto(
            id = RECITER_ID_1,
            name = FIST_RECITER_NAME,
            nameAr = FIST_RECITER_ARABIC_NAME,
            tilawahType = FIRST_TILWAH_TYPE
        )

        everySuspend { recitersDao.getReciterById(RECITER_ID_1) } returns expectedReciter

        val result = repository.getReciterById(RECITER_ID_1)

        assertEquals(RECITER_ID_1, result.id)
        assertEquals(FIST_RECITER_NAME, result.name)
        verifySuspend { recitersDao.getReciterById(RECITER_ID_1) }
    }

    @Test
    fun `getReciterById should return correct reciter when multiple exist`() = runTest {
        everySuspend { recitersDao.getReciterById(RECITER_ID_2) } returns RECITER_DTOS[1]

        val result = repository.getReciterById(RECITER_ID_2)

        assertEquals(RECITER_ID_2, result.id)
        assertEquals(SECOND_RECITER_NAME, result.name)
        assertEquals(SECOND_RECITER_ARABIC_NAME, result.arabicName)
    }

    @Test
    fun `isSurahAudioCached should return true when audio exists in filesystem`() = runTest {
        everySuspend {
            surahSoundDao.getCachedAudioPath(SURAH_ID_1, RECITER_ID_1)
        } returns LOCAL_PATH_SURAH_1

        val result = repository.isSurahAudioCached(SURAH_ID_1, RECITER_ID_1)

        assertEquals(false, result)
    }

    @Test
    fun `getAyahSoundUrl should handle Al-Fatiha first ayah correctly`() = runTest {
        everySuspend {
            surahSoundDao.getCachedAudioPath(SURAH_ID_1, RECITER_ID_1)
        } returns null

        everySuspend {
            tilawahApiService.getAyahSoundUrl(
                RECITER_ID_1, AYAH_NUMBER_1, SURAH_ID_1
            )
        } returns makeSuccessFakeResponse(REMOTE_URL_AYAH_1_SURAH_1)

        val result = repository.getAyahSoundUrl(
            ayahNumber = AYAH_NUMBER_1,
            surahNumber = SURAH_ID_1,
            reciterId = RECITER_ID_1
        )

        assertEquals(REMOTE_URL_AYAH_1_SURAH_1, result)
    }

    @Test
    fun `getAyahSoundUrl should handle last ayah of long surah`() = runTest {
        everySuspend {
            surahSoundDao.getCachedAudioPath(SURAH_ID_2, RECITER_ID_1)
        } returns null

        everySuspend {
            tilawahApiService.getAyahSoundUrl(
                RECITER_ID_1, AYAH_NUMBER_LAST_AL_BAQARAH, SURAH_ID_2
            )
        } returns makeSuccessFakeResponse(REMOTE_URL_AYAH_LAST_SURAH_2)

        val result = repository.getAyahSoundUrl(
            ayahNumber = AYAH_NUMBER_LAST_AL_BAQARAH,
            surahNumber = SURAH_ID_2,
            reciterId = RECITER_ID_1
        )

        assertEquals(REMOTE_URL_AYAH_LAST_SURAH_2, result)
    }

    @Test
    fun `getAyahSoundUrl should handle different reciters for same ayah`() = runTest {
        everySuspend {
            surahSoundDao.getCachedAudioPath(SURAH_ID_1, RECITER_ID_1)
        } returns null

        everySuspend {
            surahSoundDao.getCachedAudioPath(SURAH_ID_1, RECITER_ID_2)
        } returns null

        everySuspend {
            tilawahApiService.getAyahSoundUrl(
                RECITER_ID_1, AYAH_NUMBER_1, SURAH_ID_1
            )
        } returns makeSuccessFakeResponse(REMOTE_URL_RECITER_1_AYAH)

        everySuspend {
            tilawahApiService.getAyahSoundUrl(
                RECITER_ID_2, AYAH_NUMBER_1, SURAH_ID_1
            )
        } returns makeSuccessFakeResponse(REMOTE_URL_RECITER_2_AYAH)

        val result1 = repository.getAyahSoundUrl(AYAH_NUMBER_1, SURAH_ID_1, RECITER_ID_1)
        val result2 = repository.getAyahSoundUrl(AYAH_NUMBER_1, SURAH_ID_1, RECITER_ID_2)

        assertEquals(REMOTE_URL_RECITER_1_AYAH, result1)
        assertEquals(REMOTE_URL_RECITER_2_AYAH, result2)
    }

    @Test
    fun `saveSurahAudioToCache should handle same surah with different reciters`() = runTest {
        repository.saveSurahAudioToCache(SURAH_ID_1, RECITER_ID_1, LOCAL_PATH_RECITER_1_SURAH_1)
        repository.saveSurahAudioToCache(SURAH_ID_1, RECITER_ID_2, LOCAL_PATH_RECITER_2_SURAH_1)

        verifySuspend {
            surahSoundDao.saveSurahAudio(
                SurahAudioDto(SURAH_ID_1, RECITER_ID_1, LOCAL_PATH_RECITER_1_SURAH_1)
            )
        }
        verifySuspend {
            surahSoundDao.saveSurahAudio(
                SurahAudioDto(SURAH_ID_1, RECITER_ID_2, LOCAL_PATH_RECITER_2_SURAH_1)
            )
        }
    }

    @Test
    fun `searchForReciter should handle empty query`() = runTest {
        everySuspend { recitersDao.searchReciters(EMPTY_QUERY) } returns emptyList()

        val result = repository.searchForReciter(EMPTY_QUERY)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `searchForReciter should handle partial name match`() = runTest {
        val partialMatch = listOf(RECITER_DTOS[0])

        everySuspend { recitersDao.searchReciters(PARTIAL_RECITER_QUERY) } returns partialMatch

        val result = repository.searchForReciter(PARTIAL_RECITER_QUERY)

        assertEquals(1, result.size)
        assertEquals(FIST_RECITER_NAME, result[0].name)
    }

    @Test
    fun `searchForReciter should handle Arabic name search`() = runTest {
        val arabicMatch = listOf(RECITER_DTOS[1])

        everySuspend { recitersDao.searchReciters(ARABIC_RECITER_QUERY) } returns arabicMatch

        val result = repository.searchForReciter(ARABIC_RECITER_QUERY)

        assertEquals(1, result.size)
        assertEquals(SECOND_RECITER_ARABIC_NAME, result[0].arabicName)
    }

    @Test
    fun `getSurahAudioCachePath should return path when file exists`() = runTest {
        everySuspend {
            surahSoundDao.getCachedAudioPath(SURAH_ID_1, RECITER_ID_1)
        } returns LOCAL_PATH_SURAH_1

        val result = repository.getSurahAudioCachePath(SURAH_ID_1, RECITER_ID_1)

        assertEquals(null, result)
    }

    @Test
    fun `getRemoteSurahSoundUrl should handle network errors gracefully`() = runTest {
        everySuspend {
            tilawahApiService.getSurahSoundUrl(
                RECITER_ID_1, SURAH_ID_1
            )
        } returns makeSuccessFakeResponse(REMOTE_URL_SURAH_1)

        val result = repository.getRemoteSurahSoundUrl(SURAH_ID_1, RECITER_ID_1)

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `saveLastAyahForTilawah should save progress for different surahs`() = runTest {
        val progress1 = LastAyahForTilawah(SURAH_ID_1, AYAH_NUMBER_5)
        val progress2 = LastAyahForTilawah(SURAH_ID_2, AYAH_NUMBER_100)

        repository.saveLastAyahForTilawah(progress1)
        repository.saveLastAyahForTilawah(progress2)

        verifySuspend { tilawahDataStore.saveLastAyah(progress1) }
        verifySuspend { tilawahDataStore.saveLastAyah(progress2) }
    }

    @Test
    fun `saveDefaultReciter should save different reciter ids`() = runTest {
        repository.saveDefaultReciter(RECITER_ID_1)
        repository.saveDefaultReciter(RECITER_ID_2)

        verifySuspend { tilawahDataStore.saveDefaultReciter(RECITER_ID_1) }
        verifySuspend { tilawahDataStore.saveDefaultReciter(RECITER_ID_2) }
    }

    @Test
    fun `getDefaultReciter should return correct reciter id from datastore`() = runTest {
        everySuspend { tilawahDataStore.getDefaultReciter() } returns flowOf(RECITER_ID_2)

        val result = repository.getDefaultReciter().first()

        assertEquals(RECITER_ID_2, result)
    }

    private companion object {

        const val AL_FATIHAH_NAME_EN = "Al-Fatihah"
        const val AL_FATIHAH_NAME_AR = "الفاتحة"
        const val AL_BAQARAH_NAME_EN = "Al-Baqarah"
        const val AL_BAQARAH_NAME_AR = "Al-Baqarah"
        const val FIST_RECITER_NAME = "Abdelbasit"
        const val SECOND_RECITER_NAME = "Mishary"
        const val FIST_RECITER_ARABIC_NAME = "عبدالباسط"
        const val SECOND_RECITER_ARABIC_NAME = "مشاري"
        const val FIRST_TILWAH_TYPE = "mujawad"
        const val SECOND_TILWAH_TYPE = "mujawad"

        const val SURAH_ID_1 = 1
        const val SURAH_ID_2 = 2
        const val RECITER_ID_1 = 1
        const val RECITER_ID_2 = 2
        const val AYAH_NUMBER_1 = 1
        const val AYAH_NUMBER_5 = 5
        const val AYAH_NUMBER_100 = 100
        const val AYAH_NUMBER_OUT_OF_BOUNDS = 999
        const val AYAH_NUMBER_LAST_AL_BAQARAH = 286
        const val AYAH_COUNT_AL_FATIHAH = 7

        const val NON_EXISTENT_PATH = "/cache/nonexistent.mp3"
        const val CACHED_SURAH_PATH = "/cache/surah_1"
        const val LOCAL_PATH_SURAH_1 = "/cache/surah_1_reciter_1.mp3"
        const val LOCAL_PATH_SURAH_1_SIMPLE = "/cache/surah_1.mp3"
        const val LOCAL_PATH_SURAH_2_SIMPLE = "/cache/surah_2.mp3"
        const val LOCAL_PATH_RECITER_1_SURAH_1 = "/cache/r1_s1.mp3"
        const val LOCAL_PATH_RECITER_2_SURAH_1 = "/cache/r2_s1.mp3"
        const val EMPTY_PATH = ""

        const val REMOTE_URL_SURAH_1_RECITER_1 = "https://example.com/surah_1_reciter_1.mp3"
        const val REMOTE_URL_SURAH_1 = "https://example.com/surah_1.mp3"
        const val REMOTE_URL_SURAH_2 = "https://example.com/surah_2.mp3"
        const val REMOTE_URL_AYAH_1_SURAH_1 = "https://example.com/001_001.mp3"
        const val REMOTE_URL_AYAH_LAST_SURAH_2 = "https://example.com/002_286.mp3"
        const val REMOTE_URL_RECITER_1_AYAH = "https://example.com/reciter1_ayah.mp3"
        const val REMOTE_URL_RECITER_2_AYAH = "https://example.com/reciter2_ayah.mp3"

        const val EMPTY_QUERY = ""
        const val PARTIAL_RECITER_QUERY = "Abd"
        const val ARABIC_RECITER_QUERY = "مشاري"

        val TILAWAH_AYAH_TO_SAVE = LastAyahForTilawah(
            surahId = 1,
            number = 3
        )

        val DEFAULT_TILAWAH = LastAyahForTilawah(
            surahId = 1,
            number = 1
        )

        val SAVED_TILAWAH_PROGRESS = LastAyahForTilawah(
            surahId = 2,
            number = 5
        )

        val SURAH_DTOS: List<SurahDto> = listOf(
            SurahDto(
                number = 1,
                nameEn = AL_FATIHAH_NAME_EN,
                nameAr = AL_FATIHAH_NAME_AR,
                ayahCount = 7
            ),
            SurahDto(
                number = 2,
                nameEn = AL_BAQARAH_NAME_EN,
                nameAr = AL_BAQARAH_NAME_AR,
                ayahCount = 286
            )
        )

        val RECITER_DTOS: List<ReciterDto> = listOf(
            ReciterDto(
                id = 1,
                name = FIST_RECITER_NAME,
                nameAr = FIST_RECITER_ARABIC_NAME,
                tilawahType = FIRST_TILWAH_TYPE
            ),
            ReciterDto(
                id = 2,
                name = SECOND_RECITER_NAME,
                nameAr = SECOND_RECITER_ARABIC_NAME,
                tilawahType = SECOND_TILWAH_TYPE
            ),
        )

        val SUR_LIST = listOf(
            Surah(
                id = 1,
                order = Surah.SurahOrder.AlFatihah,
                name = AL_FATIHAH_NAME_EN,
                ayahCount = 7,
            ),
            Surah(
                id = 2,
                order = Surah.SurahOrder.AlBaqarah,
                name = AL_BAQARAH_NAME_EN,
                ayahCount = 286,
            )
        )

        val RECITER_LIST = listOf(
            Reciter(
                id = 1,
                name = FIST_RECITER_NAME,
                arabicName = FIST_RECITER_ARABIC_NAME,
                tilawahType = FIRST_TILWAH_TYPE
            ),
            Reciter(
                id = 2,
                name = SECOND_RECITER_NAME,
                arabicName = SECOND_RECITER_ARABIC_NAME,
                tilawahType = SECOND_TILWAH_TYPE
            )
        )

        val apiReciters = listOf(
            RecitersRequest(
                id = 1,
                name = FIST_RECITER_NAME,
                arabicName = FIST_RECITER_ARABIC_NAME,
                tilawahType = FIRST_TILWAH_TYPE
            ),
            RecitersRequest(
                id = 2,
                name = SECOND_RECITER_NAME,
                arabicName = SECOND_RECITER_ARABIC_NAME,
                tilawahType = SECOND_TILWAH_TYPE
            )
        )

    }
}
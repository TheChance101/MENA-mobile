package net.thechance.mena.faith.data.repository

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.database.SurahDto
import net.thechance.mena.faith.data.datastore.TilawahDataStore
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.model.LastAyahForTilawah
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuranRepositoryImplTest {

    private val mockDao: AyahDao = mock(MockMode.autofill)
    private val tilawahDataStore: TilawahDataStore = mock(MockMode.autofill)

    private val repository =
        QuranRepositoryImpl(ayahDao = mockDao, tilawahDataStore = tilawahDataStore)


    @Test
    fun `getAllSur Should return list of sur when called`() = runTest {
        // Given
        everySuspend { mockDao.getAllSur() } returns SURAH_DTOS

        // When
        val result = repository.getAllSur()

        // Then
        assertEquals(SUR_LIST, result)
    }

    @Test
    fun `getAllSur Should return empty list when database is empty`() = runTest {
        // Given
        everySuspend { mockDao.getAllSur() } returns emptyList()

        // When
        val result = repository.getAllSur()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAyatOfSurah Should return empty list when surah has no ayat`() = runTest {
        // Given
        everySuspend { mockDao.getAyatOfSurah(1) } returns emptyList()

        // When
        val result = repository.getAyatOfSurah(1)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAyatOfSurah Should return empty list when surah id is non exist`() = runTest {
        // Given
        everySuspend { mockDao.getAyatOfSurah(999) } returns emptyList()

        // When
        val result = repository.getAyatOfSurah(999)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAyatOfSurah Should return empty list when surah id is zero`() = runTest {
        // Given
        everySuspend { mockDao.getAyatOfSurah(0) } returns emptyList()

        // When
        val result = repository.getAyatOfSurah(0)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `searchForAyahInQuran Should return empty list when no ayah match the query`() = runTest {
        // Given
        everySuspend { mockDao.searchForAyahInQuran("nonexistent") } returns emptyList()

        // When
        val result = repository.searchForAyahInQuran("nonexistent")

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `searchForAyahInSurah Should return empty list when no ayah match the query in the specified surah`() = runTest {
        // Given
        everySuspend { mockDao.searchForAyahInSurah(1, "nonexistent") } returns emptyList()

        // When
        val result = repository.searchForAyahInSurah(1, "nonexistent")

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getLastAyahForTilawah should return stored ayah when datastore has value`() = runTest {
        // Given

        everySuspend { tilawahDataStore.getLastAyah() } returns SAVED_TILAWAH_PROGRESS

        // When
        val result = repository.getLastAyahForTilawah()

        // Then
        assertEquals(SAVED_TILAWAH_PROGRESS, result)
    }

    @Test
    fun `getLastAyahForTilawah should return default ayah when datastore is empty`() = runTest {
        // Given
        everySuspend { tilawahDataStore.getLastAyah() } returns null

        // When
        val result = repository.getLastAyahForTilawah()

        // Then
        assertEquals(DEFAULT_TILAWAH, result)
    }

    @Test
    fun `saveLastAyahForTilawah should call datastore saveLastAyah`() = runTest {
        // Given
        val ayahToSave = TILAWAH_AYAH_TO_SAVE

        // When
        repository.saveLastAyahForTilawah(ayahToSave)

        // Then
        verifySuspend {
            tilawahDataStore.saveLastAyah(ayahToSave)
        }
    }


    private companion object {

        const val AL_FATIHAH_NAME = "Al-Fatihah"
        const val AL_BAQARAH_NAME = "Al-Baqarah"

        val TILAWAH_AYAH_TO_SAVE = LastAyahForTilawah(
            number = 3,
            surahId = 1,
            surahName = AL_FATIHAH_NAME
        )

        val DEFAULT_TILAWAH = LastAyahForTilawah(
            number = 1,
            surahId = 1,
            surahName = "Al-Fatiha"
        )

        val SAVED_TILAWAH_PROGRESS = LastAyahForTilawah(
            number = 5,
            surahId = 2,
            surahName = AL_BAQARAH_NAME
        )
        val SURAH_DTOS: List<SurahDto> = listOf(
            SurahDto(number = 1, name = AL_FATIHAH_NAME, ayahCount = 7),
            SurahDto(number = 2, name = AL_BAQARAH_NAME, ayahCount = 286)
        )
        val SUR_LIST = listOf(
            Surah(
                id = 1,
                order = Surah.SurahOrder.AlFatihah,
                name = AL_FATIHAH_NAME,
                ayahCount = 7,
            ),
            Surah(
                id = 2,
                order = Surah.SurahOrder.AlBaqarah,
                name = AL_BAQARAH_NAME,
                ayahCount = 286,
            )
        )
    }
}
package net.thechance.mena.faith.data.repository

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.database.SurahDto
import net.thechance.mena.faith.domain.entity.Surah
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuranRepositoryImplTest {

    private val mockDao: AyahDao = mock(MockMode.autofill)
    private val repository = QuranRepositoryImpl(mockDao)


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

    private companion object {
        const val AL_FATIHAH_NAME = "Al-Fatihah"
        const val AL_BAQARAH_NAME = "Al-Baqarah"

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
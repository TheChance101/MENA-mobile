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
    fun `getAyatOfSurah Should return empty list when surah id is zero()`() = runTest {
        // Given
        everySuspend { mockDao.getAyatOfSurah(0) } returns emptyList()

        // When
        val result = repository.getAyatOfSurah(0)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAyahContent Should return content when called with valid ayah and surah`() = runTest {
        // Given
        everySuspend { mockDao.getAyahContent(1, 1) } returns BISMILLAH_TEXT

        // When
        val result = repository.getAyahContent(1, 1)

        // Then
        assertEquals(BISMILLAH_TEXT, result)
    }

    @Test
    fun `getAyahContent Should return empty string when ayah number is invalid for existing surah`() =
        runTest {
            // Given
            everySuspend { mockDao.getAyahContent(999, 1) } returns ""

            // When
            val result = repository.getAyahContent(999, 1)

            // Then
            assertEquals("", result)
        }

    @Test
    fun `getAyahContent Should return empty string when surah id is invalid for existing ayah number`() =
        runTest {
            // Given
            everySuspend { mockDao.getAyahContent(1, 999) } returns ""

            // When
            val result = repository.getAyahContent(1, 999)

            // Then
            assertEquals("", result)
        }

    @Test
    fun `getAyahContent Should return empty string when both ayah and surah id are invalid`() =
        runTest {
            // Given
            everySuspend { mockDao.getAyahContent(999, 999) } returns ""

            // When
            val result = repository.getAyahContent(999, 999)

            // Then
            assertEquals("", result)
        }

    @Test
    fun `getAyahContent Should return empty string when dao returns null content`() = runTest {
        // Given
        everySuspend { mockDao.getAyahContent(1, 1) } returns ""

        // When
        val result = repository.getAyahContent(1, 1)

        // Then
        assertEquals("", result)
    }

    private companion object {
        const val BISMILLAH_TEXT = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"
        const val AL_FATIHAH_NAME = "Al-Fatihah"
        const val AL_BAQARAH_NAME = "Al-Baqarah"

        val SURAH_DTOS: List<SurahDto> = listOf(
            SurahDto(order = 1, name = AL_FATIHAH_NAME, ayahCount = 7),
            SurahDto(order = 2, name = AL_BAQARAH_NAME, ayahCount = 286)
        )
        val SUR_LIST = listOf(
            Surah(
                id = 1,
                order = Surah.SurahOrder.AlFatihah,
                name = AL_FATIHAH_NAME,
                ayahCount = 7,
                isMakkia = true
            ),
            Surah(
                id = 2,
                order = Surah.SurahOrder.AlBaqarah,
                name = AL_BAQARAH_NAME,
                ayahCount = 286,
                isMakkia = false
            )
        )
    }
}
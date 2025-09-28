package net.thechance.mena.faith.data.repository

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.database.AyahDto
import net.thechance.mena.faith.data.database.SurahDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuranRepositoryImplTest {

    private val mockDao: AyahDao = mock(MockMode.autofill)
    private val repository = QuranRepositoryImpl(mockDao)


    @Test
    fun `getAllSur Should return list of sur when called`() = runTest {
        // Given
        everySuspend { mockDao.getAllSur() } returns SAMPLE_SURAH_DTOS

        // When
        val result = repository.getAllSur()

        // Then
        assertEquals(2, result.size)
        assertEquals(AL_FATIHAH_NAME, result[0].name)
        assertEquals(AL_BAQARAH_NAME, result[1].name)
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
    fun `getAyatOfSurah Should return list of ayat when called with valid surah id`() = runTest {
        // Given
        everySuspend { mockDao.getAyatOfSurah(1) } returns SAMPLE_AYAH_DTOS

        // When
        val result = repository.getAyatOfSurah(1)

        // Then
        assertEquals(2, result.size)
        assertEquals(BISMILLAH_TEXT, result[0].displayContent)
        assertEquals(ALHAMDULILLAH_TEXT, result[1].displayContent)
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
    fun `getAyatOfSurah Should map AyahDto to Ayah correctly`() = runTest {
        // Given
        val ayahEntity = AyahDto(
            id = 1,
            surahNumber = 1,
            surahName = AL_FATIHAH_NAME,
            surahNameAr = AL_FATIHAH_AR,
            number = 1,
            displayContent = BISMILLAH_TEXT,
            plainTextContent = BISMILLAH_PLAIN,
            lineStart = 1,
            lineEnd = 1,
            jozz = 1,
            page = 1
        )
        everySuspend { mockDao.getAyatOfSurah(1) } returns listOf(ayahEntity)

        // When
        val result = repository.getAyatOfSurah(1)

        // Then
        val ayah = result[0]
        assertEquals(1, ayah.surahId)
        assertEquals(1, ayah.number)
        assertEquals(BISMILLAH_TEXT, ayah.displayContent)
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

    companion object {
        private const val BISMILLAH_TEXT = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"
        private const val BISMILLAH_PLAIN = "Bismillah"
        private const val ALHAMDULILLAH_TEXT = "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ"
        private const val ALHAMDULILLAH_PLAIN = "Alhamdulillah"
        private const val AL_FATIHAH_NAME = "Al-Fatihah"
        private const val AL_BAQARAH_NAME = "Al-Baqarah"
        private const val AL_FATIHAH_AR = "الفاتحة"

        private val SAMPLE_SURAH_DTOS: List<SurahDto> = listOf(
            SurahDto(order = 1, name = AL_FATIHAH_NAME, ayahCount = 7),
            SurahDto(order = 2, name = AL_BAQARAH_NAME, ayahCount = 286)
        )

        private val SAMPLE_AYAH_DTOS: List<AyahDto> = listOf(
            AyahDto(
                id = 1,
                surahNumber = 1,
                surahName = AL_FATIHAH_NAME,
                surahNameAr = AL_FATIHAH_AR,
                number = 1,
                displayContent = BISMILLAH_TEXT,
                plainTextContent = BISMILLAH_PLAIN,
                lineStart = 1,
                lineEnd = 1,
                jozz = 1,
                page = 1
            ),
            AyahDto(
                id = 2,
                surahNumber = 1,
                surahName = AL_FATIHAH_NAME,
                surahNameAr = AL_FATIHAH_AR,
                number = 2,
                displayContent = ALHAMDULILLAH_TEXT,
                plainTextContent = ALHAMDULILLAH_PLAIN,
                lineStart = 2,
                lineEnd = 2,
                jozz = 1,
                page = 1
            )
        )
    }
}

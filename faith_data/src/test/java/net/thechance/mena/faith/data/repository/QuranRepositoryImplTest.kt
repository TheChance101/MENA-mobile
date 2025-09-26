package net.thechance.mena.faith.data.repository

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
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
        val surahEntities: List<SurahDto> = listOf(
            SurahDto(order = 1, name = "Al-Fatihah", ayahCount = 7),
            SurahDto(order = 2, name = "Al-Baqarah", ayahCount = 286)
        )
        everySuspend { mockDao.getAllSur() } returns surahEntities

        // When
        val result = repository.getAllSur()

        // Then
        assertEquals(2, result.size)
        assertEquals("Al-Fatihah", result[0].name)
        assertEquals("Al-Baqarah", result[1].name)
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
    fun `getAllSur Should throw exception when dao throws exception`() = runTest {
        // Given
        everySuspend { mockDao.getAllSur() } throws RuntimeException("Database error")

        // When & Then
        try {
            repository.getAllSur()
            kotlin.test.fail("Expected exception to be thrown")
        } catch (e: RuntimeException) {
            assertEquals("Database error", e.message)
        }
    }

    @Test
    fun `getAllSur Should map SurahDto to Surah correctly`() = runTest {
        // Given
        val surahEntity = SurahDto(
            order = 1,
            name = "Al-Fatihah",
            ayahCount = 7
        )
        everySuspend { mockDao.getAllSur() } returns listOf(surahEntity)

        // When
        val result = repository.getAllSur()

        // Then
        val surah = result[0]
        assertEquals(1, surah.id)
        assertEquals("Al-Fatihah", surah.name)
        assertEquals(7, surah.ayahCount)
        assertEquals(true, surah.isMakkia)
    }

    @Test
    fun `AyatOfSurah Should return list of ayat when called with valid surah id`() = runTest {
        // Given
        val ayahEntities = listOf(
            AyahDto(
                id = 1,
                surahNumber = 1,
                surahName = "Al-Fatihah",
                surahNameAr = "الفاتحة",
                number = 1,
                displayContent = "Bismillah",
                plainTextContent = "Bismillah",
                lineStart = 1,
                lineEnd = 1,
                jozz = 1,
                page = 1
            ),
            AyahDto(
                id = 2,
                surahNumber = 1,
                surahName = "Al-Fatihah",
                surahNameAr = "الفاتحة",
                number = 2,
                displayContent = "Alhamdulillah",
                plainTextContent = "Alhamdulillah",
                lineStart = 2,
                lineEnd = 2,
                jozz = 1,
                page = 1
            )
        )
        everySuspend { mockDao.getAyatOfSurah(1) } returns ayahEntities

        // When
        val result = repository.getAyatOfSurah(1)

        // Then
        assertEquals(2, result.size)
        assertEquals("Bismillah", result[0].displayContent)
        assertEquals("Alhamdulillah", result[1].plainTextContent)
    }

    @Test
    fun `getAyatOfSurah Should return empty list when surah has no ayahs`() = runTest {
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
    fun `getAyatOfSurah Should return empty list when surah id is negative`() = runTest {
        // Given
        everySuspend { mockDao.getAyatOfSurah(-1) } returns emptyList()

        // When
        val result = repository.getAyatOfSurah(-1)

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
    fun `getAyatOfSurah Should throw exception when dao throws exception`() = runTest {
        // Given
        everySuspend { mockDao.getAyatOfSurah(1) } throws RuntimeException("Database error")

        // When & Then
        try {
            repository.getAyatOfSurah(1)
            kotlin.test.fail("Expected exception to be thrown")
        } catch (e: RuntimeException) {
            assertEquals("Database error", e.message)
        }
    }

    @Test
    fun `getAyatOfSurah Should map AyahDto to Ayah correctly`() = runTest {
        // Given
        val ayahEntity = AyahDto(
            id = 1,
            surahNumber = 1,
            surahName = "Al-Fatihah",
            surahNameAr = "الفاتحة",
            number = 1,
            displayContent = "Bismillah",
            plainTextContent = "Bismillah",
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
        assertEquals("Bismillah", ayah.displayContent)
    }

    @Test
    fun `getAyahContent Should return content when called with valid ayah and surah`() = runTest {
        // Given
        everySuspend { mockDao.getAyahContent(1, 1) } returns "Bismillah"

        // When
        val result = repository.getAyahContent(1, 1)

        // Then
        assertEquals("Bismillah", result)
    }

    @Test
    fun `getAyahContent Should return empty string when ayah number is invalid for existing surah`() = runTest {
        // Given
        everySuspend { mockDao.getAyahContent(999, 1) } returns ""

        // When
        val result = repository.getAyahContent(999, 1)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `getAyahContent Should return empty string when surah id is invalid for existing ayah number`() = runTest {
        // Given
        everySuspend { mockDao.getAyahContent(1, 999) } returns ""

        // When
        val result = repository.getAyahContent(1, 999)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `getAyahContent Should return empty string when both ayah and surah id are invalid`() = runTest {
        // Given
        everySuspend { mockDao.getAyahContent(999, 999) } returns ""

        // When
        val result = repository.getAyahContent(999, 999)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `getAyahContent Should return empty string when ayah number is negative`() = runTest {
        // Given
        everySuspend { mockDao.getAyahContent(-1, 1) } returns ""

        // When
        val result = repository.getAyahContent(-1, 1)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `getAyahContent Should return empty string when ayah number is zero`() = runTest {
        // Given
        everySuspend { mockDao.getAyahContent(0, 1) } returns ""

        // When
        val result = repository.getAyahContent(0, 1)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `getAyahContent Should return empty string when surah id is negative`() = runTest {
        // Given
        everySuspend { mockDao.getAyahContent(1, -1) } returns ""

        // When
        val result = repository.getAyahContent(1, -1)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `getAyahContent Should return empty string when surah id is zero`() = runTest {
        // Given
        everySuspend { mockDao.getAyahContent(1, 0) } returns ""

        // When
        val result = repository.getAyahContent(1, 0)

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

    @Test
    fun `getAyahContent Should return empty string when DAO returns empty string content`() = runTest {
        // Given
        everySuspend { mockDao.getAyahContent(1, 1) } returns ""

        // When
        val result = repository.getAyahContent(1, 1)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `getAyahContent Should throw exception when dao throws exception`() = runTest {
        // Given
        everySuspend { mockDao.getAyahContent(1, 1) } throws RuntimeException("Database error")

        // When & Then
        try {
            repository.getAyahContent(1, 1)
            kotlin.test.fail("Expected exception to be thrown")
        } catch (e: RuntimeException) {
            assertEquals("Database error", e.message)
        }
    }

    @Test
    fun `getDao Should return dao instance when called()`() = runTest {
        // Given & When
        val dao = repository.dao

        // Then
        assertEquals(mockDao, dao)
    }
}
package net.thechance.mena.faith.data.mapper.ayahBookmark

import net.thechance.mena.faith.data.database.AyahDto
import org.junit.Assert.assertEquals
import org.junit.Test

class AyahMapperKtTest {

    @Test
    fun toAyah_shouldReturnMappedAyah_whenDtoHasValidValues() {
        // Given
        val ayahDto = AyahDto(
            id = 1,
            surahNumber = 2,
            surahName = "Al-Baqarah",
            surahNameAr = "البقرة",
            number = 255,
            displayContent = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
            plainTextContent = "الله لا إله إلا هو الحي القيوم",
            lineStart = 1,
            lineEnd = 2,
            jozz = 3,
            page = 42
        )

        // When
        val result = ayahDto.toAyah()

        // Then
        assertEquals(255, result.number)
        assertEquals(2, result.surahId)
        assertEquals("اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ", result.content)
    }

    @Test
    fun toAyah_shouldReturnMappedAyah_whenDtoHasMinimumIntegerValues() {
        val ayahDto = AyahDto(
            id = Int.MIN_VALUE,
            surahNumber = Int.MIN_VALUE,
            surahName = "Test",
            surahNameAr = "اختبار",
            number = Int.MIN_VALUE,
            displayContent = "Test content",
            plainTextContent = "Test plain",
            lineStart = Int.MIN_VALUE,
            lineEnd = Int.MIN_VALUE,
            jozz = Int.MIN_VALUE,
            page = Int.MIN_VALUE
        )

        val result = ayahDto.toAyah()

        assertEquals(Int.MIN_VALUE, result.number)
        assertEquals(Int.MIN_VALUE, result.surahId)
        assertEquals("Test content", result.content)
    }

    @Test
    fun toAyah_shouldReturnMappedAyah_whenDtoHasMaximumIntegerValues() {
        val ayahDto = AyahDto(
            id = Int.MAX_VALUE,
            surahNumber = Int.MAX_VALUE,
            surahName = "Test",
            surahNameAr = "اختبار",
            number = Int.MAX_VALUE,
            displayContent = "Test content",
            plainTextContent = "Test plain",
            lineStart = Int.MAX_VALUE,
            lineEnd = Int.MAX_VALUE,
            jozz = Int.MAX_VALUE,
            page = Int.MAX_VALUE
        )

        val result = ayahDto.toAyah()

        assertEquals(Int.MAX_VALUE, result.number)
        assertEquals(Int.MAX_VALUE, result.surahId)
        assertEquals("Test content", result.content)
    }

    @Test
    fun toAyah_shouldReturnMappedAyah_whenDisplayContentIsEmpty() {
        val ayahDto = AyahDto(
            id = 1,
            surahNumber = 1,
            surahName = "Al-Fatiha",
            surahNameAr = "الفاتحة",
            number = 1,
            displayContent = "",
            plainTextContent = "",
            lineStart = 1,
            lineEnd = 1,
            jozz = 1,
            page = 1
        )

        val result = ayahDto.toAyah()

        assertEquals(1, result.number)
        assertEquals(1, result.surahId)
        assertEquals("", result.content)
    }

    @Test
    fun toAyah_shouldReturnMappedAyah_whenDisplayContentHasSpecialCharacters() {
        val specialContent =
            "Hello 🌍 Unicode: \u0645\u0631\u062D\u0628\u0627 Emoji: 😊 Punctuation: !@#$%^&*()"
        val ayahDto = AyahDto(
            id = 1,
            surahNumber = 1,
            surahName = "Test",
            surahNameAr = "اختبار",
            number = 1,
            displayContent = specialContent,
            plainTextContent = "Test plain",
            lineStart = 1,
            lineEnd = 1,
            jozz = 1,
            page = 1
        )

        val result = ayahDto.toAyah()

        assertEquals(1, result.number)
        assertEquals(1, result.surahId)
        assertEquals(specialContent, result.content)
    }

    @Test
    fun toAyah_shouldReturnMappedAyah_whenDisplayContentIsVeryLong() {
        val longContent = "A".repeat(10000)
        val ayahDto = AyahDto(
            id = 1,
            surahNumber = 1,
            surahName = "Test",
            surahNameAr = "اختبار",
            number = 1,
            displayContent = longContent,
            plainTextContent = "Test plain",
            lineStart = 1,
            lineEnd = 1,
            jozz = 1,
            page = 1
        )

        val result = ayahDto.toAyah()

        assertEquals(1, result.number)
        assertEquals(1, result.surahId)
        assertEquals(longContent, result.content)
    }

    @Test
    fun toAyah_shouldReturnMappedAyah_whenDtoHasZeroValues() {
        val ayahDto = AyahDto(
            id = 0,
            surahNumber = 0,
            surahName = "Test",
            surahNameAr = "اختبار",
            number = 0,
            displayContent = "Test content",
            plainTextContent = "Test plain",
            lineStart = 0,
            lineEnd = 0,
            jozz = 0,
            page = 0
        )

        val result = ayahDto.toAyah()

        assertEquals(0, result.number)
        assertEquals(0, result.surahId)
        assertEquals("Test content", result.content)
    }
}

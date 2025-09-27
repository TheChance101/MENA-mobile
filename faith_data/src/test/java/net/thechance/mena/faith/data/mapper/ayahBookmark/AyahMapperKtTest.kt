package net.thechance.mena.faith.data.mapper.ayahBookmark

import net.thechance.mena.faith.data.database.AyahDto
import net.thechance.mena.faith.domain.entity.Ayah
import org.junit.Assert.assertEquals
import org.junit.Test

class AyahMapperKtTest {

    @Test
    fun `toAyah should return mapped ayah when dto has valid values`() {
        val result = TestData.validAyahDto.toAyah()
        assertEquals(TestData.expectedValidAyah, result)
    }

    @Test
    fun `toAyah should return mapped ayah when dto has minimum integer values`() {
        val result = TestData.minIntAyahDto.toAyah()
        assertEquals(TestData.expectedMinIntAyah, result)
    }

    @Test
    fun `toAyah should return mapped ayah when dto has maximum integer values`() {
        val result = TestData.maxIntAyahDto.toAyah()
        assertEquals(TestData.expectedMaxIntAyah, result)
    }

    @Test
    fun `toAyah should return mapped ayah when displayContent is empty`() {
        val result = TestData.emptyContentAyahDto.toAyah()
        assertEquals(TestData.expectedEmptyContentAyah, result)
    }

    @Test
    fun `toAyah should return mapped ayah when displayContent is very long`() {
        val result = TestData.longContentAyahDto.toAyah()
        assertEquals(TestData.expectedLongContentAyah, result)
    }

    @Test
    fun `toAyah should return mapped ayah when dto has zero values`() {
        val result = TestData.zeroValuesAyahDto.toAyah()
        assertEquals(TestData.expectedZeroValuesAyah, result)
    }

    companion object TestData {
        val validAyahDto = AyahDto(
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
        val expectedValidAyah = Ayah(
            number = 255,
            surahId = 2,
            content = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ"
        )

        val minIntAyahDto = AyahDto(
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
        val expectedMinIntAyah = Ayah(
            number = Int.MIN_VALUE,
            surahId = Int.MIN_VALUE,
            content = "Test content"
        )

        val maxIntAyahDto = minIntAyahDto.copy(
            id = Int.MAX_VALUE,
            surahNumber = Int.MAX_VALUE,
            number = Int.MAX_VALUE,
            displayContent = "Test content",
            lineStart = Int.MAX_VALUE,
            lineEnd = Int.MAX_VALUE,
            jozz = Int.MAX_VALUE,
            page = Int.MAX_VALUE
        )
        val expectedMaxIntAyah = Ayah(
            number = Int.MAX_VALUE,
            surahId = Int.MAX_VALUE,
            content = "Test content"
        )

        val emptyContentAyahDto = validAyahDto.copy(
            surahNumber = 1,
            number = 1,
            displayContent = "",
            plainTextContent = "",
            lineStart = 1,
            lineEnd = 1,
            jozz = 1,
            page = 1
        )
        val expectedEmptyContentAyah = Ayah(
            number = 1,
            surahId = 1,
            content = ""
        )


        // Long content
        val longContent = "A".repeat(10000)
        val longContentAyahDto = validAyahDto.copy(
            surahNumber = 1,
            number = 1,
            displayContent = longContent
        )
        val expectedLongContentAyah = Ayah(
            number = 1,
            surahId = 1,
            content = longContent
        )

        // Zero values
        val zeroValuesAyahDto = validAyahDto.copy(
            id = 0,
            surahNumber = 0,
            number = 0,
            displayContent = "Test content",
            plainTextContent = "Test plain",
            lineStart = 0,
            lineEnd = 0,
            jozz = 0,
            page = 0
        )
        val expectedZeroValuesAyah = Ayah(
            number = 0,
            surahId = 0,
            content = "Test content"
        )
    }
}

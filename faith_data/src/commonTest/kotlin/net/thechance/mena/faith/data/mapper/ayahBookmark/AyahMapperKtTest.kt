package net.thechance.mena.faith.data.mapper.ayahBookmark

import net.thechance.mena.faith.data.database.AyahDto
import net.thechance.mena.faith.data.mapper.toAyah
import net.thechance.mena.faith.domain.entity.Ayah
import kotlin.test.Test
import kotlin.test.assertEquals

class AyahMapperKtTest {

    @Test
    fun `toAyah should return mapped ayah when dto has valid values`() {
        val result = validAyahDto.toAyah()
        assertEquals(expectedValidAyah, result)
    }

    @Test
    fun `toAyah should return mapped ayah when dto has minimum integer values`() {
        val result = minIntAyahDto.toAyah()
        assertEquals(expectedMinIntAyah, result)
    }

    @Test
    fun `toAyah should return mapped ayah when dto has maximum integer values`() {
        val result = maxIntAyahDto.toAyah()
        assertEquals(expectedMaxIntAyah, result)
    }

    @Test
    fun `toAyah should return mapped ayah when displayContent is empty`() {
        val result = emptyContentAyahDto.toAyah()
        assertEquals(expectedEmptyContentAyah, result)
    }

    @Test
    fun `toAyah should return mapped ayah when displayContent is very long`() {
        val result = longContentAyahDto.toAyah()
        assertEquals(expectedLongContentAyah, result)
    }

    @Test
    fun `toAyah should return mapped ayah when dto has zero values`() {
        val result = zeroValuesAyahDto.toAyah()
        assertEquals(expectedZeroValuesAyah, result)
    }

    companion object TestData {
        val validAyahDto = AyahDto(
            id = 1,
            surahNumber = 2,
            surahNameEn = "Al-Baqarah",
            surahNameAr = "البقرة",
            number = 255,
            content = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
            plainContent = "الله لا إله إلا هو الحي القيوم",
            lineStart = 1,
            lineEnd = 2,
            jozz = 3,
            page = 42
        )
        val expectedValidAyah = Ayah(
            number = 255,
            surahId = 2,
            content = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
            plainContent = "الله لا إله إلا هو الحي القيوم"
        )

        val minIntAyahDto = AyahDto(
            id = Int.MIN_VALUE,
            surahNumber = Int.MIN_VALUE,
            surahNameEn = "Test",
            surahNameAr = "اختبار",
            number = Int.MIN_VALUE,
            content = "Test content",
            plainContent = "Test plain",
            lineStart = Int.MIN_VALUE,
            lineEnd = Int.MIN_VALUE,
            jozz = Int.MIN_VALUE,
            page = Int.MIN_VALUE
        )
        val expectedMinIntAyah = Ayah(
            number = Int.MIN_VALUE,
            surahId = Int.MIN_VALUE,
            content = "Test content",
            plainContent = "Test plain"
        )

        val maxIntAyahDto = minIntAyahDto.copy(
            id = Int.MAX_VALUE,
            surahNumber = Int.MAX_VALUE,
            number = Int.MAX_VALUE,
            content = "Test content",
            lineStart = Int.MAX_VALUE,
            lineEnd = Int.MAX_VALUE,
            jozz = Int.MAX_VALUE,
            page = Int.MAX_VALUE
        )
        val expectedMaxIntAyah = Ayah(
            number = Int.MAX_VALUE,
            surahId = Int.MAX_VALUE,
            content = "Test content",
            plainContent = "Test plain"
        )

        val emptyContentAyahDto = validAyahDto.copy(
            surahNumber = 1,
            number = 1,
            content = "",
            plainContent = "",
            lineStart = 1,
            lineEnd = 1,
            jozz = 1,
            page = 1
        )
        val expectedEmptyContentAyah = Ayah(
            number = 1,
            surahId = 1,
            content = "",
            plainContent = ""
        )


        // Long content
        val longContent = "A".repeat(10000)
        val longContentAyahDto = validAyahDto.copy(
            surahNumber = 1,
            number = 1,
            content = longContent
        )
        val expectedLongContentAyah = Ayah(
            number = 1,
            surahId = 1,
            content = longContent,
            plainContent = "الله لا إله إلا هو الحي القيوم"
        )

        // Zero values
        val zeroValuesAyahDto = validAyahDto.copy(
            id = 0,
            surahNumber = 0,
            number = 0,
            content = "Test content",
            plainContent = "Test plain",
            lineStart = 0,
            lineEnd = 0,
            jozz = 0,
            page = 0
        )
        val expectedZeroValuesAyah = Ayah(
            number = 0,
            surahId = 0,
            content = "Test content",
            plainContent = "Test plain"
        )
    }
}

package net.thechance.mena.faith.data.mapper.ayahBookmark

import net.thechance.mena.faith.data.database.SurahDto
import net.thechance.mena.faith.data.mapper.toSurah
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.identity.domain.util.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class SurahMapperKtTest {

    @Test
    fun `toSurah should return mapped surah when dto has valid values`() {
        val result = validSurahDto.toSurah(AppLanguage.ENGLISH)
        assertEquals(expectedValidSurah, result)
    }

    @Test
    fun `toSurah should return mapped surah when dto has minimum valid order`() {
        val result = minOrderSurahDto.toSurah(AppLanguage.ENGLISH)
        assertEquals(expectedMinOrderSurah, result)
    }

    @Test
    fun `toSurah should return mapped surah when dto has maximum valid order`() {
        val result = maxOrderSurahDto.toSurah(AppLanguage.ENGLISH)
        assertEquals(expectedMaxOrderSurah, result)
    }

    @Test
    fun `toSurah should return mapped surah when dto has empty name`() {
        val result = emptyNameSurahDto.toSurah(AppLanguage.ENGLISH)
        assertEquals(expectedEmptyNameSurah, result)
    }

    @Test
    fun `toSurah should throw exception when dto has order not in SurahOrder`() {
        assertFailsWith<NoSuchElementException> {
            invalidOrderSurahDto.toSurah(AppLanguage.ENGLISH)
        }
    }

    @Test
    fun `toSurah should throw exception when dto has zero order if zero not valid`() {
        if (Surah.SurahOrder.entries.none { it.order == 0 }) {
            assertFailsWith<NoSuchElementException> {
                zeroOrderSurahDto.toSurah(AppLanguage.ENGLISH)
            }
        }
    }

    @Test
    fun `toSurah should throw exception when dto has negative order`() {
        assertFailsWith<NoSuchElementException> {
            negativeOrderSurahDto.toSurah(AppLanguage.ENGLISH)
        }
    }

    @Test
    fun `toSurah should return correct mappings when mapping all SurahOrders`() {
        Surah.SurahOrder.entries.forEach { entry ->
            val dto = SurahDto(
                number = entry.order,
                nameEn = "Surah ${entry.order}",
                nameAr = "${entry.order}سورة ",
                ayahCount = 1
            )
            val expected = Surah(
                id = entry.order,
                order = entry,
                name = "Surah ${entry.order}",
                ayahCount = 1,
            )

            val result = dto.toSurah(AppLanguage.ENGLISH)
            assertEquals(expected, result)
        }
    }

    companion object TestData {
        private val firstOrder = Surah.SurahOrder.entries.minOf { it.order }
        private val lastOrder = Surah.SurahOrder.entries.maxOf { it.order }

        val validSurahDto =
            SurahDto(number = firstOrder, nameEn = "Al-Fatiha", nameAr = "الفاتحة", ayahCount = 1)
        val expectedValidSurah = Surah(
            id = firstOrder,
            order = Surah.SurahOrder.entries.first { it.order == firstOrder },
            name = "Al-Fatiha",
            ayahCount = 1,
        )

        val minOrderSurahDto = SurahDto(
            number = firstOrder,
            nameEn = "Al-Fatiha",
            nameAr = "الفاتحة",
            ayahCount = 1
        )
        val expectedMinOrderSurah = expectedValidSurah.copy(
            name = "Al-Fatiha"
        )

        val maxOrderSurahDto = SurahDto(
            number = lastOrder,
            nameEn = "Al-Fatiha",
            nameAr = "الفاتحة",
            ayahCount = 1
        )
        val expectedMaxOrderSurah = Surah(
            id = lastOrder,
            order = Surah.SurahOrder.entries.first { it.order == lastOrder },
            name = "Al-Fatiha",
            ayahCount = 1,
        )

        val emptyNameSurahDto =
            SurahDto(number = firstOrder, nameEn = "", nameAr = "", ayahCount = 1)
        val expectedEmptyNameSurah = expectedValidSurah.copy(name = "")

        val invalidOrderSurahDto = SurahDto(
            number = lastOrder + 1000,
            nameEn = "Al-Fatiha",
            nameAr = "الفاتحة",
            ayahCount = 1
        )
        val zeroOrderSurahDto = SurahDto(
            number = 0,
            nameEn = "Al-Fatiha",
            nameAr = "الفاتحة",
            ayahCount = 1
        )
        val negativeOrderSurahDto = SurahDto(
            number = -1,
            nameEn = "Al-Fatiha",
            nameAr = "الفاتحة",
            ayahCount = 1
        )
    }
}



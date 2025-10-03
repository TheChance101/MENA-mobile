package net.thechance.mena.faith.data.mapper.ayahBookmark

import net.thechance.mena.faith.data.database.SurahDto
import net.thechance.mena.faith.data.mapper.toSurah
import net.thechance.mena.faith.domain.entity.Surah
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class SurahMapperKtTest {

    @Test
    fun `toSurah should return mapped surah when dto has valid values`() {
        val result = validSurahDto.toSurah()
        assertEquals(expectedValidSurah, result)
    }

    @Test
    fun `toSurah should return mapped surah when dto has minimum valid order`() {
        val result = minOrderSurahDto.toSurah()
        assertEquals(expectedMinOrderSurah, result)
    }

    @Test
    fun `toSurah should return mapped surah when dto has maximum valid order`() {
        val result = maxOrderSurahDto.toSurah()
        assertEquals(expectedMaxOrderSurah, result)
    }

    @Test
    fun `toSurah should return mapped surah when dto has empty name`() {
        val result = emptyNameSurahDto.toSurah()
        assertEquals(expectedEmptyNameSurah, result)
    }

    @Test
    fun `toSurah should throw exception when dto has order not in SurahOrder`() {
        assertFailsWith<NoSuchElementException> {
            invalidOrderSurahDto.toSurah()
        }
    }

    @Test
    fun `toSurah should throw exception when dto has zero order if zero not valid`() {
        if (Surah.SurahOrder.entries.none { it.order == 0 }) {
            assertFailsWith<NoSuchElementException> {
                zeroOrderSurahDto.toSurah()
            }
        }
    }

    @Test
    fun `toSurah should throw exception when dto has negative order`() {
        assertFailsWith<NoSuchElementException> {
            negativeOrderSurahDto.toSurah()
        }
    }

    @Test
    fun `toSurah should return correct mappings when mapping all SurahOrders`() {
        Surah.SurahOrder.entries.forEach { entry ->
            val dto = SurahDto(number = entry.order, name = "Surah ${entry.order}", ayahCount = 1)
            val expected = Surah(
                id = entry.order,
                order = entry,
                name = "Surah ${entry.order}",
                ayahCount = 1,
            )

            val result = dto.toSurah()
            assertEquals(expected, result)
        }
    }

    companion object TestData {
        private val firstOrder = Surah.SurahOrder.entries.minOf { it.order }
        private val lastOrder = Surah.SurahOrder.entries.maxOf { it.order }

        val validSurahDto = SurahDto(number = firstOrder, name = "Al-Fatiha", ayahCount = 1)
        val expectedValidSurah = Surah(
            id = firstOrder,
            order = Surah.SurahOrder.entries.first { it.order == firstOrder },
            name = "Al-Fatiha",
            ayahCount = 1,
        )

        val minOrderSurahDto = SurahDto(
            number = firstOrder,
            name = "Test Surah",
            ayahCount = 1
        )
        val expectedMinOrderSurah = expectedValidSurah.copy(
            name = "Test Surah"
        )

        val maxOrderSurahDto = SurahDto(
            number = lastOrder,
            name = "Test Surah",
            ayahCount = 1
        )
        val expectedMaxOrderSurah = Surah(
            id = lastOrder,
            order = Surah.SurahOrder.entries.first { it.order == lastOrder },
            name = "Test Surah",
            ayahCount = 1,
        )

        val emptyNameSurahDto = SurahDto(number = firstOrder, name = "", ayahCount = 1)
        val expectedEmptyNameSurah = expectedValidSurah.copy(name = "")

        val invalidOrderSurahDto = SurahDto(
            number = lastOrder + 1000,
            name = "InvalidOrder",
            ayahCount = 1
        )
        val zeroOrderSurahDto = SurahDto(
            number = 0,
            name = "ZeroOrder",
            ayahCount = 1
        )
        val negativeOrderSurahDto = SurahDto(
            number = -1,
            name = "NegativeOrder",
            ayahCount = 1
        )
    }
}



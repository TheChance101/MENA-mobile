package net.thechance.mena.faith.data.mapper.ayahBookmark

import net.thechance.mena.faith.data.database.SurahDto
import net.thechance.mena.faith.domain.entity.Surah
import org.junit.Test

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
            val dto = SurahDto(order = entry.order, nameEn = "Surah ${entry.order}")
            val expected = Surah(
                id = entry.order,
                order = entry,
                name = "Surah ${entry.order}",
                ayahCount = 1,
                isMakkia = entry.isMakkia
            )

            val result = dto.toSurah()
            assertEquals(expected, result)
        }
    }

    companion object TestData {
        private val firstOrder = Surah.SurahOrder.entries.minOf { it.order }
        private val lastOrder = Surah.SurahOrder.entries.maxOf { it.order }

        val validSurahDto = SurahDto(order = firstOrder, nameEn = "Al-Fatiha")
        val expectedValidSurah = Surah(
            id = firstOrder,
            order = Surah.SurahOrder.entries.first { it.order == firstOrder },
            name = "Al-Fatiha",
            ayahCount = 1,
            isMakkia = Surah.SurahOrder.entries.first { it.order == firstOrder }.isMakkia
        )

        val minOrderSurahDto = SurahDto(
            order = firstOrder,
            nameEn = "Test Surah"
        )
        val expectedMinOrderSurah = expectedValidSurah.copy(
            name = "Test Surah"
        )

        val maxOrderSurahDto = SurahDto(
            order = lastOrder,
            nameEn = "Test Surah"
        )
        val expectedMaxOrderSurah = Surah(
            id = lastOrder,
            order = Surah.SurahOrder.entries.first { it.order == lastOrder },
            name = "Test Surah",
            ayahCount = 1,
            isMakkia = Surah.SurahOrder.entries.first { it.order == lastOrder }.isMakkia
        )

        val emptyNameSurahDto = SurahDto(order = firstOrder, nameEn = "")
        val expectedEmptyNameSurah = expectedValidSurah.copy(name = "")

        val invalidOrderSurahDto = SurahDto(
            order = lastOrder + 1000,
            nameEn = "InvalidOrder"
        )
        val zeroOrderSurahDto = SurahDto(
            order = 0,
            nameEn = "ZeroOrder"
        )
        val negativeOrderSurahDto = SurahDto(
            order = -1,
            nameEn = "NegativeOrder"
        )
    }
}



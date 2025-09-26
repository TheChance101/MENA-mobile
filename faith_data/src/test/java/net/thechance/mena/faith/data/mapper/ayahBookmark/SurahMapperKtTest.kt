package net.thechance.mena.faith.data.mapper.ayahBookmark

import net.thechance.mena.faith.data.database.SurahDto
import net.thechance.mena.faith.domain.entity.Surah
import org.junit.Test

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SurahMapperKtTest {

    @Test
    fun toSurah_shouldReturnMappedSurah_whenDtoHasValidValues() {
        // Given
        val validOrder = Surah.SurahOrder.entries.first().order
        val surahDto = createSurahDto(order = validOrder, nameEn = "Al-Fatiha")

        // When
        val result = surahDto.toSurah()

        // Then
        assertEquals(validOrder, result.id)
        assertEquals(Surah.SurahOrder.entries.first { it.order == validOrder }, result.order)
        assertEquals("Al-Fatiha", result.name)
        assertEquals(1, result.ayahCount)
        assertEquals(result.order.isMakkia, result.isMakkia)
    }

    @Test
    fun toSurah_shouldReturnMappedSurah_whenDtoHasMinimumValidOrder() {
        val minOrder = Surah.SurahOrder.entries.minOf { it.order }
        val surahDto = createSurahDto(order = minOrder)

        val result = surahDto.toSurah()

        assertEquals(minOrder, result.id)
        assertEquals(Surah.SurahOrder.entries.first { it.order == minOrder }, result.order)
    }

    @Test
    fun toSurah_shouldReturnMappedSurah_whenDtoHasMaximumValidOrder() {
        val maxOrder = Surah.SurahOrder.entries.maxOf { it.order }
        val surahDto = createSurahDto(order = maxOrder)

        val result = surahDto.toSurah()

        assertEquals(maxOrder, result.id)
        assertEquals(Surah.SurahOrder.entries.first { it.order == maxOrder }, result.order)
    }

    @Test
    fun toSurah_shouldReturnMappedSurah_whenDtoHasEmptyName() {
        val validOrder = Surah.SurahOrder.entries.first().order
        val surahDto = createSurahDto(order = validOrder, nameEn = "")

        val result = surahDto.toSurah()

        assertEquals("", result.name)
    }

    @Test
    fun toSurah_shouldThrowException_whenDtoHasOrderNotInSurahOrder() {
        val invalidOrder = (Surah.SurahOrder.entries.maxOf { it.order } + 1000)
        val surahDto = createSurahDto(order = invalidOrder)

        assertFailsWith<NoSuchElementException> {
            surahDto.toSurah()
        }
    }

    @Test
    fun toSurah_shouldThrowException_whenDtoHasZeroOrderIfZeroNotValid() {
        val surahDto = createSurahDto(order = 0)

        if (Surah.SurahOrder.entries.none { it.order == 0 }) {
            assertFailsWith<NoSuchElementException> {
                surahDto.toSurah()
            }
        }
    }

    @Test
    fun toSurah_shouldThrowException_whenDtoHasNegativeOrder() {
        val surahDto = createSurahDto(order = -1)

        assertFailsWith<NoSuchElementException> {
            surahDto.toSurah()
        }
    }


    @Test
    fun toSurah_shouldReturnCorrectMappings_whenMappingAllSurahOrders() {
        Surah.SurahOrder.entries.forEach { entry ->
            val surahDto = createSurahDto(order = entry.order, nameEn = "Surah ${entry.order}")

            val result = surahDto.toSurah()

            assertEquals(entry.order, result.id)
            assertEquals(entry, result.order)
            assertEquals("Surah ${entry.order}", result.name)
            assertEquals(1, result.ayahCount)
            assertEquals(entry.isMakkia, result.isMakkia)
        }
    }

    private fun createSurahDto(
        order: Int,
        nameEn: String = "Test Surah",
    ) = SurahDto(
        order = order,
        nameEn = nameEn,
    )

}

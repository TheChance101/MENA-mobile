package net.thechance.mena.dukan.data.repository.mapper


import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.repository.dto.PageResponseDto
import net.thechance.mena.dukan.domain.util.PagedResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PagedResponseMapperKtTest {

    @Test
    fun `toDomain maps content and pagination correctly`() = runTest {
        val dto: PageResponseDto<String> = PageResponseDto(
            content = listOf("A", "B", "C"),
            number = 2,
            size = 3,
            totalPages = 5,
            totalElements = 15,
            first = false,
            last = false
        )

        val result = dto.toDomain { it.lowercase() }

        assertEquals(listOf("a", "b", "c"), result.items)
        assertEquals(2, result.currentPage)
        assertEquals(5, result.totalPages)
        assertTrue(result.hasNext)
        assertTrue(result.hasPrevious)
    }

    @Test
    fun `toDomain marks first page correctly`() = runTest {
        val dto: PageResponseDto<String> = PageResponseDto(
            content = emptyList<String>(),
            number = 0,
            size = 0,
            totalPages = 1,
            totalElements = 0,
            first = true,
            last = false
        )

        val result: PagedResult<String> = dto.toDomain { it }

        assertTrue(result.hasNext)
        assertTrue(!result.hasPrevious)
    }

    @Test
    fun `toDomain marks last page correctly`() = runTest {
        val dto = PageResponseDto(
            content = emptyList<String>(),
            number = 1,
            size = 0,
            totalPages = 1,
            totalElements = 0,
            first = false,
            last = true
        )

        val result = dto.toDomain { it }

        assertTrue(!result.hasNext)
        assertTrue(result.hasPrevious)
    }

}
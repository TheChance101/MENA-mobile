package net.thechance.mena.dukan.data.repository.mapper


import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.data.dto.PageResponseDto
import net.thechance.mena.dukan.data.mapper.toDomain
import kotlin.test.Test
import kotlin.test.assertEquals

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
    }
}
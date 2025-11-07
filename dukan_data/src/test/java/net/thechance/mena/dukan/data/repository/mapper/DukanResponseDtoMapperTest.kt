package net.thechance.mena.dukan.data.repository.mapper

import net.thechance.mena.dukan.data.dto.dukan.DukanResponseDto
import net.thechance.mena.dukan.data.mapper.toEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class DukanResponseDtoMapperTest {
    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `toDomainPreview SHOULD map fields correctly`() {
        // Arrange
        val id = Uuid.parse("123e4567-e89b-12d3-a456-426614174000")
        val dto = DukanResponseDto(
            id = id,
            name = "Dukan Name",
            imageUrl = "https://example.com/image.png",
            isFavorite = false
        )

        // Act
        val result = dto.toEntity()

        // Assert
        assertEquals(id, result.id)
        assertEquals("Dukan Name", result.name)
        assertEquals("https://example.com/image.png", result.imageUrl)
        assertEquals(false, result.isFavorite)
    }
}
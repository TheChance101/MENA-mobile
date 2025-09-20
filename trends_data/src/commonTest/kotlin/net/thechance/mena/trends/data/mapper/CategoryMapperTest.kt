package net.thechance.mena.trends.data.mapper

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import net.thechance.mena.trends.data.dto.CategoryDto
import kotlin.test.Test

class CategoryMapperTest {

    @Test
    fun `categoryDto toEntity() should map correctly`() {
        val dto = CategoryDto(id = "uuid 1", name = "Sport", emoji = "⚽")
        val category = dto.toEntity()

        assertThat(category?.id).isEqualTo("uuid 1")
        assertThat(category?.name).isEqualTo("Sport")
        assertThat(category?.emoji).isEqualTo("⚽")
    }

    @Test
    fun `categoryDto with null values toEntity() should map to default values`() {
        val dto = CategoryDto()
        val category = dto.toEntity()

        assertThat(category?.id).isNull()
        assertThat(category?.name).isNull()
        assertThat(category?.emoji).isNull()
    }

    @Test
    fun `List of categoryDto toEntity() should map to List of Category correctly`() {
        val dtos = listOf(
            CategoryDto(id = "uuid 1", name = "Sport", emoji = "⚽"),
            CategoryDto(id = "uuid 2", name = "Music", emoji = "🎵")
        )

        val categories = dtos.toEntityList()

        assertThat(categories.first().id).isEqualTo("uuid 1")
        assertThat(categories.first().name).isEqualTo("Sport")
        assertThat(categories.first().emoji).isEqualTo("⚽")

        assertThat(categories.last().id).isEqualTo("uuid 2")
        assertThat(categories.last().name).isEqualTo("Music")
        assertThat(categories.last().emoji).isEqualTo("🎵")

        assertThat(categories.size).isEqualTo(2)
    }
}
package net.thechance.mena.trends.data.mapper

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import net.thechance.mena.trends.data.dto.CategoryDto
import net.thechance.mena.trends.domain.entity.Category
import kotlin.test.Test

internal class CategoryMapperTest {

    @Test
    fun `categoryDto toEntity() should map correctly`() {
        val dto = CategoryDto(id = "uuid 1", name = "Sport", emoji = "⚽")
        val category = dto.toEntity()

        assertThat(category?.id).isEqualTo("uuid 1")
        assertThat(category?.name).isEqualTo("Sport")
        assertThat(category?.emoji).isEqualTo("⚽")
    }

    @Test
    fun `categoryDto with null values and valid id toEntity() should map to default values`() {
        val dto =  CategoryDto(id = "uuid 1")
        val category = dto.toEntity()

        assertThat(category?.id).isEqualTo("uuid 1")
        assertThat(category?.name).isEqualTo("")
        assertThat(category?.emoji).isEqualTo("")
    }

    @Test
    fun `categoryDto with null id toEntity() should return null`() {
        val dto =  CategoryDto(id = null, name = "Sport", emoji = "⚽")
        val category = dto.toEntity()

        assertThat(category).isNull()
    }

    @Test
    fun `List of categoryDto toEntity() should map to List of Category correctly`() {
        val dtos = listOf(
            CategoryDto(id = "uuid 1", name = "Sport", emoji = "⚽"),
            CategoryDto(id = "uuid 2", name = "Music", emoji = "🎵")
        )

        val categories = dtos.toEntityList()

        val expectedFirst = Category(id = "uuid 1", name = "Sport", emoji = "⚽")
        val expectedLast = Category(id = "uuid 2", name = "Music", emoji = "🎵")

        assertThat(categories.first()).isEqualTo(expectedFirst)
        assertThat(categories.last()).isEqualTo(expectedLast)
        assertThat(categories).hasSize(2)
    }
}
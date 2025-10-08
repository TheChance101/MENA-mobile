package net.thechance.mena.trends.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import net.thechance.mena.trends.data.dto.CategoryDto
import kotlin.test.Test

internal class CategoryMapperTest {

    @Test
    fun `categoryDto toEntity() should map correctly`() {
        val category = categoryDto.toEntity()

        assertThat(category?.id).isEqualTo(categoryDto.id)
        assertThat(category?.name).isEqualTo(categoryDto.name)
        assertThat(category?.emoji).isEqualTo(categoryDto.emoji)
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
        val categories = categoriesDto.toEntityList()

        assertThat(categories.first().id).isEqualTo(categoriesDto.first().id)
        assertThat(categories.first().name).isEqualTo(categoriesDto.first().name)
        assertThat(categories.first().emoji).isEqualTo(categoriesDto.first().emoji)

        assertThat(categories.last().id).isEqualTo(categoriesDto.last().id)
        assertThat(categories.last().name).isEqualTo(categoriesDto.last().name)
        assertThat(categories.last().emoji).isEqualTo(categoriesDto.last().emoji)
        assertThat(categories.size).isEqualTo(2)
    }

    private companion object {
        val categoryDto = CategoryDto(
            id = "uuid 1",
            name = "Sport",
            emoji = "⚽"
        )
        val categoriesDto = listOf(
            categoryDto,
            CategoryDto(id = "uuid 2", name = "Music", emoji = "🎵")
        )
    }
}
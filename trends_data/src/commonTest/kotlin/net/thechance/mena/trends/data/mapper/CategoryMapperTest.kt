package net.thechance.mena.trends.data.mapper

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import net.thechance.mena.trends.data.remote.dto.CategoryDto
import net.thechance.mena.trends.data.remote.mapper.toEntity
import net.thechance.mena.trends.data.remote.mapper.toEntityList
import net.thechance.mena.trends.domain.entity.Category
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
    fun `categoryDto with null values and valid id toEntity() should map to default values`() {
        val dto =  CategoryDto(id = "uuid 1")
        val category = dto.toEntity()

        assertThat(category?.id).isEqualTo("uuid 1")
        assertThat(category?.name).isEqualTo("")
        assertThat(category?.emoji).isEqualTo("")
    }


    @Test
    fun `List of categoryDto toEntity() should map to List of Category correctly`() {
        val categories = categoriesDto.toEntityList()
        val expectedFirst = Category(id = "uuid 1", name = "Sport", emoji = "⚽", isSelected = false)
        val expectedLast = Category(id = "uuid 2", name = "Music", emoji = "🎵", isSelected = false)

        assertThat(categories.first()).isEqualTo(expectedFirst)
        assertThat(categories.last()).isEqualTo(expectedLast)
        assertThat(categories).hasSize(2)
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
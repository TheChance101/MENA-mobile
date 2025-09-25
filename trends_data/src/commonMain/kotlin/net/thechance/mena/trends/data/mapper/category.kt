package net.thechance.mena.trends.data.mapper

import net.thechance.mena.trends.data.dto.CategoryDto
import net.thechance.mena.trends.domain.entity.Category

internal fun CategoryDto.toEntity(): Category? {
    if (id == null) return null
    return Category(
        id = id,
        name = name.orEmpty(),
        emoji = emoji.orEmpty()
    )
}

internal fun List<CategoryDto>.toEntityList(): List<Category> = mapNotNull(CategoryDto::toEntity)
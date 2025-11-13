package net.thechance.mena.trends.data.remote.mapper

import net.thechance.mena.trends.data.remote.dto.CategoryDto
import net.thechance.mena.trends.data.util.orFalse
import net.thechance.mena.trends.domain.entity.Category

internal fun CategoryDto.toEntity(): Category? {
    return Category(
        id = id,
        name = name.orEmpty(),
        emoji = emoji.orEmpty(),
        isSelected = isSelected.orFalse()
    )
}

internal fun List<CategoryDto>.toEntityList(): List<Category> = mapNotNull(CategoryDto::toEntity)
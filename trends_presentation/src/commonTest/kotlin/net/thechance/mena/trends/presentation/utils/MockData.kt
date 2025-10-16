package net.thechance.mena.trends.presentation.utils

import net.thechance.mena.trends.domain.entity.Category

val category = Category(id = "1", name = "Category 1", emoji = "🫡", isSelected = false)

val categories = listOf(
    category,
    Category(id = "2", name = "Category 2", emoji = "🔥", isSelected = false),
    Category(id = "3", name = "Category 3", emoji = "👻", isSelected = false)
)
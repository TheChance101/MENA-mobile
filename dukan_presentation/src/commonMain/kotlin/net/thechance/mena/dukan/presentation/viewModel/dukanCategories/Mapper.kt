package net.thechance.mena.dukan.presentation.viewModel.dukanCategories

import net.thechance.mena.dukan.domain.entity.Category
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Category.toUiState(): DukanCategoriesUiState.CategoryUiState {
    return DukanCategoriesUiState.CategoryUiState(
        id = id.toString(),
        name = name,
        imageUrl = imageUrl
    )
}
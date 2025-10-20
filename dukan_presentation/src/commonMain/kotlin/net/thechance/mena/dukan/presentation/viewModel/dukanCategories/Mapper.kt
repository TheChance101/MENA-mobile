package net.thechance.mena.dukan.presentation.viewModel.dukanCategories

import net.thechance.mena.dukan.domain.entity.Category
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Category.toUiState(): CategoryUiState {
    return CategoryUiState(
        id = id.toString(),
        name = name,
        imageUrl = imageUrl
    )
}

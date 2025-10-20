package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

import net.thechance.mena.dukan.domain.entity.DukanPreview

fun DukanPreview.toUiState() = CategoryDukansUiState.DukanUiState(
    id = id,
    name = name,
    imageUrl = imageUrl,
    isFavorite = false
)
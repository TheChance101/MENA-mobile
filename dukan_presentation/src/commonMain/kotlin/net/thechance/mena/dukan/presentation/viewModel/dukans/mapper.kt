package net.thechance.mena.dukan.presentation.viewModel.dukans

import net.thechance.mena.dukan.domain.entity.DukanPreview

fun DukanPreview.toUiState() = DukanUiState(
    id = id,
    name = name,
    imageUrl = imageUrl,
    isFavorite = false
)

package net.thechance.mena.dukan.presentation.viewModel.dukans

import net.thechance.mena.dukan.domain.entity.Dukan

fun Dukan.toUiState() = DukanUiState(
    id = id,
    name = name,
    imageUrl = imageUrl,
    isFavorite = false
)
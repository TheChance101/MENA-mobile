package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

import net.thechance.mena.dukan.domain.entity.Dukan
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Dukan.toUiState() = CategoryDukansUiState.DukanUiState(
    id = id.toString(),
    name = name,
    imageUrl = imageUrl,
    isFavorite = false
)
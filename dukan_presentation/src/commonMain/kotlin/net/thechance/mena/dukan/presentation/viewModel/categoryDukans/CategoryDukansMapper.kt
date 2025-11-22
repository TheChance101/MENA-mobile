package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.model.DukanPreview
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Dukan.toUiState() = CategoryDukansUiState.DukanUiState(
    id = id.toString(),
    name = name,
    imageUrl = imageUrl,
    isFavorite = isFavorite
)

@OptIn(ExperimentalUuidApi::class)
fun DukanPreview.toUiState()=CategoryDukansUiState.DukanUiState(
    id = id.toString(),
    name = name,
    imageUrl = imageUrl,
    isFavorite = isFavorite
)
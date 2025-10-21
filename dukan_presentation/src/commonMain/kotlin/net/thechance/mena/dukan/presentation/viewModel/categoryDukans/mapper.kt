package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

import net.thechance.mena.dukan.domain.entity.DukanPreview
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun DukanPreview.toUiState() = CategoryDukansUiState.DukanUiState(
    id = id.toString(),
    name = name,
    imageUrl = imageUrl,
    isFavorite = false
)
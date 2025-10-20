package net.thechance.mena.dukan.presentation.viewModel.dukans

import net.thechance.mena.dukan.domain.entity.DukanPreview
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun DukanPreview.toUiState() = DukanUiState(
    id = id.toString(),
    name = name,
    imageUrl = imageUrl,
    isFavorite = false
)

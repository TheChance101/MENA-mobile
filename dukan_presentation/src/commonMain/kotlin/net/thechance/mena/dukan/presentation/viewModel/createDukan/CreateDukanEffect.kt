package net.thechance.mena.dukan.presentation.viewModel.createDukan

sealed interface CreateDukanEffect {
    data object NavigateToImageCropScreen : CreateDukanEffect
    data object NavigateNext : CreateDukanEffect
}
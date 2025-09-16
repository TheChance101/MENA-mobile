package net.thechance.mena.dukan.presentation.viewModel.createDukan

sealed interface CreateDukanEffect {
    data object NavigateNext : CreateDukanEffect
    data object NavigateBack : CreateDukanEffect
}
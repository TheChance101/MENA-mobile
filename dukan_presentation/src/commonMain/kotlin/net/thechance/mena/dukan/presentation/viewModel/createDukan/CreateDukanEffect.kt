package net.thechance.mena.dukan.presentation.viewModel.createDukan

sealed interface CreateDukanEffect {
    data object NavigateBack : CreateDukanEffect
    data class NavigateToPending(val name: String): CreateDukanEffect
}
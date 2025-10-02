package net.thechance.mena.dukan.presentation.viewModel.createShelf

sealed interface CreateShelfEffect {
    object NavigateBack : CreateShelfEffect
    object NavigateToManageDukan : CreateShelfEffect
}
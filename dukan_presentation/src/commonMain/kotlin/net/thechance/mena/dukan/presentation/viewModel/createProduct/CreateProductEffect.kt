package net.thechance.mena.dukan.presentation.viewModel.createProduct

sealed interface CreateProductEffect {
    data object NavigateBack : CreateProductEffect
    data object NavigateToManageDukanProducts: CreateProductEffect
}
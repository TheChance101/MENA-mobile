package net.thechance.mena.dukan.presentation.viewModel.editProduct

sealed interface EditProductEffect {
    data object NavigateBack : EditProductEffect
    data object NavigateToManageDukanProducts: EditProductEffect
}

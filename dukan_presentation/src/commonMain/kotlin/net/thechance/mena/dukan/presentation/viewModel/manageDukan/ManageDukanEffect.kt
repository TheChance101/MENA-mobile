package net.thechance.mena.dukan.presentation.viewModel.manageDukan

sealed interface ManageDukanEffect {
    object NavigateBack : ManageDukanEffect
    object NavigateToAddShelf : ManageDukanEffect
    data class NavigateToManageShelf(val shelfId: String, val shelfTitle: String) :
        ManageDukanEffect

    object NavigateToAddProduct : ManageDukanEffect
    object NavigateToProductDetails : ManageDukanEffect
}

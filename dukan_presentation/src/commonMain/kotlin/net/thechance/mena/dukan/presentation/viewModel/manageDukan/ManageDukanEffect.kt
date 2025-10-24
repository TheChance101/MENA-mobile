package net.thechance.mena.dukan.presentation.viewModel.manageDukan

sealed interface ManageDukanUiEffect {
    object NavigateBack : ManageDukanUiEffect
    object NavigateToAddShelf : ManageDukanUiEffect
    data class NavigateToManageShelf(val shelfId: String, val shelfTitle: String) :
        ManageDukanUiEffect

    object NavigateToAddProduct : ManageDukanUiEffect
    object NavigateToProductDetails : ManageDukanUiEffect
}
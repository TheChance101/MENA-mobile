package net.thechance.mena.dukan.presentation.viewModel.manageDukan

sealed interface ManageDukanUiEffect {
    object NavigateBack : ManageDukanUiEffect
    object NavigateToAddShelf : ManageDukanUiEffect
    data class NavigateToManageShelf(val shelfId: String, val shelfTitle: String) :
        ManageDukanUiEffect

    object NavigateToAddProduct : ManageDukanUiEffect
    data class NavigateToEditProduct(val productId: String) : ManageDukanUiEffect
    object NavigateToProductDetails : ManageDukanUiEffect
}
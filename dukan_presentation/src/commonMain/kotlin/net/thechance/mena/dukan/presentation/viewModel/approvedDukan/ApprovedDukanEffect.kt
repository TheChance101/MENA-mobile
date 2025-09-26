package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

sealed interface ApprovedDukanEffect {
    object NavigateBack : ApprovedDukanEffect
    object NavigateToAddShelf : ApprovedDukanEffect
    object NavigateToEditShelf : ApprovedDukanEffect
    object NavigateToAddProduct : ApprovedDukanEffect
    object NavigateToProductDetails : ApprovedDukanEffect
}

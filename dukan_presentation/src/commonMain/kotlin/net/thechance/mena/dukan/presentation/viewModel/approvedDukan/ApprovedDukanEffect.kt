package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

sealed interface ApprovedDukanEffect {
    object NavigateBack : ApprovedDukanEffect
    object NavigateToAddProduct : ApprovedDukanEffect
    object NavigateToEditShelf : ApprovedDukanEffect
}

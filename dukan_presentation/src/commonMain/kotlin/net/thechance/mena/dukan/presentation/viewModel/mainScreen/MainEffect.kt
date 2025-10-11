package net.thechance.mena.dukan.presentation.viewModel.mainScreen

sealed class MainEffect {
    object NavigateToAddDukanScreen : MainEffect()
    object NavigateToPendingDukanScreen : MainEffect()
    object NavigateToManageDukanScreen : MainEffect()
    data class NavigateToDukansUnderCategory(
        val categoryId: String,
        val categoryTitle: String
    ) : MainEffect()
}
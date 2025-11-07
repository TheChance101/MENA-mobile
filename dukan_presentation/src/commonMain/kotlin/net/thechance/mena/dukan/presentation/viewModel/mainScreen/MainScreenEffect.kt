package net.thechance.mena.dukan.presentation.viewModel.mainScreen

sealed class MainScreenEffect {
    object NavigateToAddDukanScreen : MainScreenEffect()
    object NavigateToPendingDukanScreen : MainScreenEffect()
    object NavigateToManageDukanScreen : MainScreenEffect()
    object NavigateToDukansCategoriesScreen : MainScreenEffect()
    data class NavigateToDukansScreenByCategory(
        val categoryId: String,
        val categoryName: String
    ) : MainScreenEffect()
    data class NavigateToSelectedDukan(val dukanId: String) : MainScreenEffect()
    object NavigateToSearchScreen : MainScreenEffect()
}
package net.thechance.mena.dukan.presentation.viewModel.mainScreen

sealed class MainScreenEffect {
    object NavigateToAddDukanScreen : MainScreenEffect()
    object NavigateToPendingDukanScreen : MainScreenEffect()
    object NavigateToManageDukanScreen : MainScreenEffect()

    object NavigateCategoryToScreen : MainScreenEffect()


    data class NavigateToDukansScreenByCategory(val categoryId: String, val categoryName: String) :
        MainScreenEffect()

    data class NavigateSelectedDukan(val dukanId: String) : MainScreenEffect()

}
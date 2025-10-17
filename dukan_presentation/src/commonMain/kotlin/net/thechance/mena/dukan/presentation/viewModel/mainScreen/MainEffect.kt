package net.thechance.mena.dukan.presentation.viewModel.mainScreen

sealed class MainEffect {
    object NavigateToAddDukanScreen : MainEffect()
    object NavigateToPendingDukanScreen : MainEffect()
    object NavigateToManageDukanScreen : MainEffect()

    object NavigateCategoryToScreen : MainEffect()


    data class NavigateToDukansScreenByCategory(val categoryId: String, val categoryName: String) :
        MainEffect()

    data class NavigateSelectedDukan(val dukanId: String) : MainEffect()

}
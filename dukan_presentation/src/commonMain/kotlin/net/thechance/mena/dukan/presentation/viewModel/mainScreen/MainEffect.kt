package net.thechance.mena.dukan.presentation.viewModel.mainScreen

sealed class MainEffect {
    object NavigateToAddDukanScreen : MainEffect()
    object NavigateToPendingDukanScreen : MainEffect()
    object NavigateToManageDukanScreen : MainEffect()

    object NavigateCategoryToScreen : MainEffect()


    data class NavigateToDukansScreenByCategory(val categoryId: String) : MainEffect()

    data class NavigateSelectedNearsetDukan(val dukanId: String) : MainEffect()

    data class NavigateSelectedEditorPickDukan(val dukanId: String) : MainEffect()
}
package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

sealed interface CategoryDukansEffects {
    object NavigateBack : CategoryDukansEffects
    data class NavigateToDukanDetails(val dukanId: String) : CategoryDukansEffects
}

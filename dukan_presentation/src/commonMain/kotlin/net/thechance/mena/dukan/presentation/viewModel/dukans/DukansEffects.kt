package net.thechance.mena.dukan.presentation.viewModel.dukans

sealed interface DukansEffects {
    object NavigateBack : DukansEffects
    data class NavigateToDukanDetails(val dukanId: String) : DukansEffects
}

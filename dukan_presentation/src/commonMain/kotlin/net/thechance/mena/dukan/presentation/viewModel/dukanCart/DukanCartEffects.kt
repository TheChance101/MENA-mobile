package net.thechance.mena.dukan.presentation.viewModel.dukanCart

sealed class DukanCartEffects {
    object NavigateBack : DukanCartEffects()
    data class NavigateToDukanDetails(val dukanId: String) : DukanCartEffects()
    data class NavigateToCheckout(val dukanId: String) : DukanCartEffects()
}
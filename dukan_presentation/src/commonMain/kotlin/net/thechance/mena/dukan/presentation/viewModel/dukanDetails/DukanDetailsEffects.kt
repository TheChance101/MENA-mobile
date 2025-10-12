package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

sealed class DukanDetailsEffects {
    object NavigateBack : DukanDetailsEffects()
    data class NavigateToViewAllShelfProducts(val id: String, val name: String) : DukanDetailsEffects()
    data class NavigateToViewDukanOnMap(val latitude: Double, val longitude: Double) : DukanDetailsEffects()
}
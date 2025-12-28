package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap


internal interface PickLocationScreenInteractionListener {
    fun onMapClick(coordinates: CoordinatesUiState)
    fun onMoveCamera(coordinates: CoordinatesUiState)
    fun onGpsClick()
    fun onConfirmClick()
    fun onBackClick()
}



package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap


internal interface PickLocationScreenInteractionListener {
    fun onClickMap(coordinates: CoordinatesUiState)
    fun onMoveCamera(coordinates: CoordinatesUiState)
    fun onClickGps()
    fun onClickConfirm()
    fun onClickBack()
}



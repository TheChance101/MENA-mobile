package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap

import net.thechance.mena.faith.presentation.feature.mosque.create.CreateMosqueUiState

interface PickLocationScreenInteractionListener {
    fun onClickMap(coordinates: CreateMosqueUiState)
    fun onMoveCamera(coordinates: CreateMosqueUiState)
    fun onClickGps()
    fun onClickConfirm()
    fun onClickBack()
}



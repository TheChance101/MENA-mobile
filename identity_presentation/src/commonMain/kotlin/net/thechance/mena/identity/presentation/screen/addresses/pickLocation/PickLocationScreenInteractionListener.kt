package net.thechance.mena.identity.presentation.screen.addresses.pickLocation

import net.thechance.mena.identity.presentation.base.BaseInteractionListener
import net.thechance.mena.identity.presentation.screen.addresses.shared.CoordinatesUiState

interface PickLocationScreenInteractionListener : BaseInteractionListener {
    fun onClickMap(coordinates: CoordinatesUiState)
    fun onMoveCamera(coordinates: CoordinatesUiState)
    fun onClickGps()
    fun onClickConfirm()
    fun onClickBack()
}



package net.thechance.mena.identity.presentation.feature.locationFlow.pickLocation

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener
import net.thechance.mena.identity.presentation.feature.locationFlow.shared.CoordinatesUiState

interface PickLocationScreenInteractionListener : BaseInteractionListener {
    fun onClickMap(coordinates: CoordinatesUiState)
    fun onMoveCamera(coordinates: CoordinatesUiState)
    fun onClickGps()
    fun onClickConfirm()
    fun onClearErrorMessage()
    fun onClickBack()
}



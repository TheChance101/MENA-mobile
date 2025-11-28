package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap

import io.github.dellisd.spatialk.geojson.Position
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState
import net.thechance.mena.faith.presentation.feature.mosque.create.CreateMosqueUiState

data class PickLocationScreenUIState(
    val mosqueLocation: MosqueUiState.Coordinate? = null,
    val animateToCurrentLocation: Boolean = false,
    val showAnchor: Boolean = false,
    val address: String = "",
    val isLoading: Boolean = false,
    val isConfirmEnabled: Boolean = false,
    val isGpsButtonLoading: Boolean = false,
    val isMainAddress: Boolean = false
)


fun CreateMosqueUiState.toPosition() = Position(
    latitude = this.mosqueLocation?.latitude ?: 0.0,
    longitude = this.mosqueLocation?.longitude ?: 0.0
)

fun Position.toCoordinatesUiState() = CreateMosqueUiState(
    mosqueLocation = MosqueUiState.Coordinate(
        latitude = latitude,
        longitude = longitude
    )
)
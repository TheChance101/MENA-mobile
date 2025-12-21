package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap

import io.github.dellisd.spatialk.geojson.Position
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState
import net.thechance.mena.identity.domain.model.Coordinates

internal data class PickLocationScreenUIState(
    val mosqueLocation: CoordinatesUiState = CoordinatesUiState(0.0, 0.0),
    val animateToCurrentLocation: Boolean = false,
    val showAnchor: Boolean = false,
    val address: String = "",
    val isConfirmEnabled: Boolean = false,
    val isGpsButtonLoading: Boolean = false
)

@Serializable
data class AddressModel(
    val address: String = "",
    val coordinates: CoordinatesUiState? = null
)

@Serializable
data class CoordinatesUiState(
    val latitude: Double,
    val longitude: Double
)

fun Coordinates.toUiState() = CoordinatesUiState(
    latitude = latitude,
    longitude = longitude
)

fun CoordinatesUiState.toPosition() = Position(
    latitude = latitude,
    longitude = longitude
)

fun Position.toCoordinatesUiState() = CoordinatesUiState(
    latitude = latitude,
    longitude = longitude
)

fun MosqueUiState.Coordinate.toCoordinatesUiState() = CoordinatesUiState(
    latitude = latitude,
    longitude = longitude
)

fun CoordinatesUiState.toCoordinates() = MosqueUiState.Coordinate(
    latitude = latitude,
    longitude = longitude
)

fun AddressModel.toAddressJsonString(): String {
    return Json.encodeToString(this)
}

fun convertAddressStringToAddressModel(address: String?): AddressModel?{
    return if(address == null) null else Json.decodeFromString<AddressModel>(address)
}
package net.thechance.mena.faith.presentation.feature.mosque

import net.thechance.mena.faith.domain.entity.Mosque
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal data class NearbyMosquesMapUiState(
    val mosques: List<MosqueUiState> = emptyList(),
    val mosquesSearchResults: List<MosqueUiState> = emptyList(),
    val centerOfMap: Coordinate? = null,
    val isLoading: Boolean = true,
    val isMosqueBottomSheetVisible: Boolean = false,
    val isSearchResultsBottomSheetVisible: Boolean = false,
    val isSearchButtonVisible: Boolean = false,
    val isNoMosquesCardVisible: Boolean = false,
    val error: String? = null,
    val query: String = "",
)

@OptIn(ExperimentalUuidApi::class)
internal data class MosqueUiState(
    val id: Uuid,
    val name: String,
    val imageUrl: String,
    val distance: Double,
    val coordinate: Coordinate
)

internal data class Coordinate(
    val latitude: Double,
    val longitude: Double,
)

@OptIn(ExperimentalUuidApi::class)
internal fun Mosque.toUiState(distance: Double): MosqueUiState {
    return MosqueUiState(
        id = id,
        name = name,
        imageUrl = imageUrl,
        distance = distance,
        coordinate = Coordinate(
            latitude = coordinates.latitude,
            longitude = coordinates.longitude
        )
    )
}

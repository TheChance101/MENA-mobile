package net.thechance.mena.faith.presentation.feature.mosque

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import net.thechance.mena.faith.domain.entity.Mosque
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal data class NearbyMosquesMapUiState(
    val mosques: List<MosqueUiState> = emptyList(),
    val selectedMosque: MosqueUiState? = fakeMosqueDetails,
    val mosquesSearchResults: Flow<PagingData<MosqueUiState>>? = null,
    val userLocation: Coordinate? = null,
    val centerOfMap: Coordinate? = null,
    val isLoading: Boolean = true,
    val canMove: Boolean = true,
    val isMosqueBottomSheetVisible: Boolean = true,
    val isSearchResultsBottomSheetVisible: Boolean = false,
    val isSearchButtonVisible: Boolean = false,
    val isNoMosquesCardVisible: Boolean = false,
    val error: String? = null,
    val query: String = "",
)

@OptIn(ExperimentalUuidApi::class)
data class MosqueUiState(
    val id: Uuid,
    val name: String,
    val imageUrl: String,
    val distance: Double,
    val coordinate: Coordinate
)

data class Coordinate(
    val latitude: Double,
    val longitude: Double,
)

@OptIn(ExperimentalUuidApi::class)
internal val fakeMosqueDetails = MosqueUiState(
    id = Uuid.parse("8d0440c0-8a20-4c65-98f2-94b5a0f9bca4"),
    name = "Al Noor Grand Mosque",
    imageUrl = "https://images.unsplash.com/photo-1524499982521-1ffd58dd89ea?auto=format&fit=crop&w=1200&q=80",
    distance = 0.4,
    coordinate = Coordinate(
        latitude = 25.2048,
        longitude = 55.2708
    )
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
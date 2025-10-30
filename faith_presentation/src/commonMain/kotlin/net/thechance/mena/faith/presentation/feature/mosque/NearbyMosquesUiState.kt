package net.thechance.mena.faith.presentation.feature.mosque

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal data class NearbyMosquesMapUiState(
    val mosques: List<MosqueUiState> = emptyList(),
    val mosquesSearchResults: List<MosqueUiState> = emptyList(),
    val centerOfMap: Coordinate? = null,
    val isLoading: Boolean = true,
    val isMosqueBottomSheetVisible: Boolean = false,
    val isSearchButtonVisible: Boolean = false,
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
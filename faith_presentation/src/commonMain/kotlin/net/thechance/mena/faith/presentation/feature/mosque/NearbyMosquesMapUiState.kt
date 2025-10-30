package net.thechance.mena.faith.presentation.feature.mosque

internal data class MosqueMapUiState(
    val mosques: List<MosqueUiState> = emptyList(),
    val mosquesSearchResults: List<MosqueUiState> = emptyList(),
    val currentUserLocation: UserLocationUiState? = null,
    val isLoading: Boolean = true,
    val isMosqueBottomSheetVisible: Boolean = false,
    val isSearchButtonVisible: Boolean = false,
    val error: String? = null,
    val query: String = "",
)

internal data class MosqueUiState(
    val id: String,
    val name: String,
    val imageUrl: String,
    val distance: Double,
    val latitude: Double,
    val longitude: Double,
) {
    fun calculateDistance(currentUserLocation: UserLocationUiState) {
//        TODO("Not yet implemented")
    }
}

internal data class UserLocationUiState(
    val latitude: Double,
    val longitude: Double,
)
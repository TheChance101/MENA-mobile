package net.thechance.mena.faith.presentation.feature.mosque

import org.jetbrains.compose.resources.StringResource

internal interface NearbyMosquesInteractionListener {
    fun onBackClick()
    fun onAddMosqueClick()
    fun getUserLocation()
    fun onViewMosqueDetailsClick(mosque: MosqueUiState)
    fun onViewOnMapClick(coordinate: Coordinate)
    fun onSearchByCoordinates(coordinate: Coordinate)
    fun onSearchResultClick(mosque: MosqueUiState)
    fun onQueryChange(query: String)
    fun onSearchSubmit()
    fun onDismissSearchBottomSheet()
    fun selectMosque(mosque: MosqueUiState)
    fun unselectMosque()
    fun showSuccessMessage(message: StringResource)
    fun onCameraMove()
    fun onMapIdle(latitude: Double, longitude: Double)
}
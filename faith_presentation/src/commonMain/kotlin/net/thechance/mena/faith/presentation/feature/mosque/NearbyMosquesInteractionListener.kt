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
    fun changeCenterOfMap(coordinate: Coordinate)
    fun onQueryChange(query: String)
    fun changeMapMovement(canMove: Boolean)
    fun onSearchSubmit()
    fun changeSearchButtonVisibility(isVisible: Boolean)
    fun onDismissSearchBottomSheet()
    fun selectMosque(mosque: MosqueUiState)
    fun unselectMosque()
    fun showSuccessMessage(message: StringResource)
}
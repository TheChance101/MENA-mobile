package net.thechance.mena.faith.presentation.feature.mosque

internal interface NearbyMosquesInteractionListener {
    fun onBackClick()
    fun onAddMosqueClick()
    fun onCurrentUserLocationClick()
    fun onViewMosqueDetailsClick(mosque: MosqueUiState)
    fun onViewMosqueOnMapClick(coordinate: Coordinate)
    fun onSearchByCoordinatesClick(coordinate: Coordinate)
    fun onSearchResultClick(mosque: MosqueUiState)
    fun mapPositionChanged(coordinate: Coordinate)
    fun onQueryChange(query: String)
    fun changeSearchButtonVisibility(isVisible: Boolean)
    fun onDismissSearchBottomSheet()
}
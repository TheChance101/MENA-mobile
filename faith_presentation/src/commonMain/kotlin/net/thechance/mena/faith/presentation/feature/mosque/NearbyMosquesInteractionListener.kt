package net.thechance.mena.faith.presentation.feature.mosque

internal interface NearbyMosquesInteractionListener {
    fun onBackClick()
    fun onAddMosqueClick()
    fun getUserLocation()
    fun onViewMosqueDetailsClick(mosque: MosqueUiState)
    fun onViewOnMapClick(coordinate: Coordinate)
    fun onSearchByCoordinatesClick(coordinate: Coordinate)
    fun onSearchResultClick(mosque: MosqueUiState)
    fun mapPositionChanged(coordinate: Coordinate)
    fun onQueryChange(query: String)
    fun changeSearchButtonVisibility(isVisible: Boolean)
    fun onDismissSearchBottomSheet()
    fun selectMosque(mosque: MosqueUiState)
    fun unselectMosque()
}
package net.thechance.mena.faith.presentation.feature.mosque

internal interface NearbyMosquesMapInteractionListener {
    fun onBackClick()
    fun onAddMosqueClick()
    fun onCurrentUserLocationClick()
    fun onViewMosqueDetailsClick(mosque: MosqueUiState)
    fun onViewMosqueOnMapClick(latitude: Double, longitude: Double)
    fun onSearchByCoordinatesClick(latitude: Double, longitude: Double)
    fun mapPositionChanged()
    fun onQueryChange(query: String)
}
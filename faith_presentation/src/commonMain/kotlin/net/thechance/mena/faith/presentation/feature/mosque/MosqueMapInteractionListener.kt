package net.thechance.mena.faith.presentation.feature.mosque

internal interface MosqueMapInteractionListener {
    fun onClickBack()
    fun onClickAddMosque()
    fun onClickCurrentUserLocation()
    fun onClickViewMosqueDetails(mosque: MosqueUiState)
    fun onClickViewMosqueOnMap(latitude: Double, longitude: Double)
    fun onClickSearchByCoordinates(latitude: Double, longitude: Double)
    fun mapPositionChanged()
    fun onQueryChange(query: String)
}
package net.thechance.mena.faith.presentation.feature.quran.search.ayah

interface SearchInteractionListener {
    fun onQueryChange(query: String)
    fun onClearQueryClick()
    fun onBackClick()
    fun onSearchResultClick(surahId: Int, ayahId: Int)
}
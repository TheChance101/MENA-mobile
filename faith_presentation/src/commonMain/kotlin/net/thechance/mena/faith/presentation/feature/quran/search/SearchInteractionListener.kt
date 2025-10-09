package net.thechance.mena.faith.presentation.feature.quran.search

interface SearchInteractionListener {
    fun onQueryChange(query: String)
    fun clearQuery()
    fun onBackClick()
    fun onSearchResultClick(surahId: Int, ayahId: Int)
}
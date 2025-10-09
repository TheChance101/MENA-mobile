package net.thechance.mena.faith.presentation.feature.quran.search

interface SearchInteractionListener {
    fun onQueryChange(query: String)
    fun onClearQuery()
    fun onBackClick()
    fun onSearchResultClick(surahId: Int, ayahId: Int)
}
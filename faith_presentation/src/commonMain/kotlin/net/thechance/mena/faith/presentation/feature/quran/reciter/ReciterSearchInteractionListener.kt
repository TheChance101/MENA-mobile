package net.thechance.mena.faith.presentation.feature.quran.reciter

interface ReciterSearchInteractionListener {
    fun onBackClick()
    fun onClearQueryClick()
    fun onQueryChange(query: String)
}
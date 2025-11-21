package net.thechance.mena.faith.presentation.feature.quran.reciter.reciterSelection

interface ReciterSelectionListener {
    fun onBackClick()
    fun onClearQueryClick()
    fun onQueryChange(query: String)
    fun onSelectReciterClick(reciterId: Int)
}
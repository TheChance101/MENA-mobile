package net.thechance.mena.faith.presentation.feature.quran.reciter.surahRecitersScreen

interface SurahRecitersListener {
    fun onBackClick()
    fun onDownloadClick(reciterId: Int)
    fun onSelectReciterClick(reciterId: Int)
    fun onQueryChange(query: String)
    fun onClearQueryClick()
}

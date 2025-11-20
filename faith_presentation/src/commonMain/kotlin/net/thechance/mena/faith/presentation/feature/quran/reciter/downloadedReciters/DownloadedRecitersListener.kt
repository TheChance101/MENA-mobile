package net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters

interface DownloadedRecitersListener {
    fun onBackClick()
    fun onQueryChange(query: String)
    fun onClearQueryClick()
    fun onSelectReciterClick(reciterId: Int)
    fun onDeleteReciterAudioClick(reciterId: Int)
}

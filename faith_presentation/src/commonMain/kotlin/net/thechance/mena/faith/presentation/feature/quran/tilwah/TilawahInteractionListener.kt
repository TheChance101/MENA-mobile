package net.thechance.mena.faith.presentation.feature.quran.tilwah

interface TilawahInteractionListener {
    fun onBackClick()
    fun onSearchClick()

    fun onDownloadClick(reciterId: Int)

    fun onSelectReciterClick(reciterId: Int)
}
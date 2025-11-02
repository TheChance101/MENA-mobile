package net.thechance.mena.faith.presentation.feature.downloadedSur

interface DownloadedSurInteractionListener {
    fun onReciterSettingsClick()

    fun onDownloadedSurahClick(surahId: Int)

    fun onDeleteDownloadedSurahClick(surahId: Int)

    fun onBackClick()
}

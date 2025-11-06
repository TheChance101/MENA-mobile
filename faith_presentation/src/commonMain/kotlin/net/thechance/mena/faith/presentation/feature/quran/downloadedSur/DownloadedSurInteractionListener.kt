package net.thechance.mena.faith.presentation.feature.quran.downloadedSur

interface DownloadedSurInteractionListener {
    fun onReciterSettingsClick()

    fun onDownloadedSurahClick(surahId: Int)

    fun onBackClick()

    fun onDeleteSurahClick(surahId: Int)

    fun onDismissDeleteConfirmationDialog()

    fun onConfirmDeleteDownloadedSurahClick()
}

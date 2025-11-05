package net.thechance.mena.faith.presentation.feature.downloadedSur

interface DownloadedSurInteractionListener {
    fun onReciterSettingsClick()

    fun onDownloadedSurahClick(surahId: Int)

    fun onBackClick()

    fun onDeleteSurahClick(surahId: Int)

    fun onDismissDeleteConfirmationDialog()

    fun onConfirmDeleteDownloadedSurahClick()
}

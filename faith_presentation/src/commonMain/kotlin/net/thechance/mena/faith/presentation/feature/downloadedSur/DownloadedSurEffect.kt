package net.thechance.mena.faith.presentation.feature.downloadedSur

sealed interface DownloadedSurEffect {
    data object NavigateBack : DownloadedSurEffect

    data object NavigateToRecitersScreen : DownloadedSurEffect

    data class NavigateToDownloadedSurahReciterScreen(
        val surahId: Int,
    ) : DownloadedSurEffect
}

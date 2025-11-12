package net.thechance.mena.faith.presentation.feature.quran.downloadedSur

sealed interface DownloadedSurEffect {
    data object NavigateBack : DownloadedSurEffect

    data object NavigateToRecitersScreen : DownloadedSurEffect

    data class NavigateToDownloadedSurahReciterScreen(
        val surahId: Int,
    ) : DownloadedSurEffect
}

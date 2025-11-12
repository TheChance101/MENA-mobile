package net.thechance.mena.faith.presentation.feature.quran.downloadedSur

sealed interface DownloadedSurEffect {
    data object NavigateBack : DownloadedSurEffect

    data class NavigateToRecitersScreen(val surahId: Int) : DownloadedSurEffect

    data class NavigateToDownloadedSurahReciterScreen(
        val surahId: Int,
    ) : DownloadedSurEffect
}

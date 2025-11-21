package net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters

sealed interface DownloadedRecitersEffect {
    data object NavigateBack : DownloadedRecitersEffect
}

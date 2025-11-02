package net.thechance.mena.faith.presentation.feature.downloadedSur

import org.jetbrains.compose.resources.DrawableResource

data class DownloadedSurUiState(
    val surDetails: List<SurahDetailsUiState> = emptyList(),
) {
    data class SurahDetailsUiState(
        val id: Int,
        val arabicNameImg: DrawableResource,
        val surahName: String,
        val downloadedReciters: List<String>,
    )
}

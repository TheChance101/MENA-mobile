package net.thechance.mena.faith.presentation.feature.quran.downloadedSur

import org.jetbrains.compose.resources.DrawableResource

data class DownloadedSurUiState(
    val surDetails: List<SurahDetailsUiState> = emptyList(),
    val selectedSurahForDelete: Int? = null,
    val showDeleteConfirmationDialog: Boolean = false,
) {
    data class SurahDetailsUiState(
        val id: Int,
        val arabicNameImg: DrawableResource,
        val surahName: String,
        val downloadedReciters: List<String>,
    )
}

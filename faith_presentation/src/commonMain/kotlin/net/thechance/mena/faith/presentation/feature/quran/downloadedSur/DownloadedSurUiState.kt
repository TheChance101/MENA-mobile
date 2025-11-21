package net.thechance.mena.faith.presentation.feature.quran.downloadedSur

import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.model.DownlodedSur

data class DownloadedSurUiState(
    val surDetails: List<SurahDetailsUiState> = emptyList(),
    val selectedSurahForDelete: Int? = null,
    val showDeleteConfirmationDialog: Boolean = false,
) {
    data class SurahDetailsUiState(
        val id: Int,
        val arabicNameImg: Surah.SurahOrder,
        val surahName: String,
        val recitersName: List<String>,
    )
}

fun DownlodedSur.toUiState(): DownloadedSurUiState.SurahDetailsUiState =
    DownloadedSurUiState.SurahDetailsUiState(
        id = id,
        arabicNameImg = arabicNameImg,
        surahName = surahName,
        recitersName = recitersName,
    )


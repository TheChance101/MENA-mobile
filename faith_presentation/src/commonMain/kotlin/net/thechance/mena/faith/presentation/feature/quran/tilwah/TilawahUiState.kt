package net.thechance.mena.faith.presentation.feature.quran.tilwah

data class TilawahUiState(
    val reciter: List<String> = emptyList(),
    val recitingType: String = "",
    val isDownloaded: Boolean = true
)

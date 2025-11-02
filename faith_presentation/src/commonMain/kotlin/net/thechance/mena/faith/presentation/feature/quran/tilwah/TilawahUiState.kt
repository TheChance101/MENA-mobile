package net.thechance.mena.faith.presentation.feature.quran.tilwah

data class TilawahUiState(
    val reciters: List<ReciterData> = emptyList(),
    val selectedReciterId: Int? = null,
    )

data class ReciterData(
    val id: Int,
    val name: String,
    val recitingType: String,
    val isDownloaded: Boolean
)
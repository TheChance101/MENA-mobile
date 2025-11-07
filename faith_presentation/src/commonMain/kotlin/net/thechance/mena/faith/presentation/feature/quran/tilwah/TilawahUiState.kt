package net.thechance.mena.faith.presentation.feature.quran.tilwah

data class TilawahUiState(
    val reciters: List<ReciterUi> = emptyList(),
    val selectedReciterId: Int? = null,
    val selectedReciterForDelete: Int? = null,
    val showDeleteConfirmationDialog: Boolean = false,
) {
    data class ReciterUi(
        val id: Int,
        val name: String,
        val recitingType: String,
        val isDownloaded: Boolean,
    )
}

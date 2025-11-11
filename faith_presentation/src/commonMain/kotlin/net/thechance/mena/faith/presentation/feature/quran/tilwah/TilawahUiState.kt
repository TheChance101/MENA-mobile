package net.thechance.mena.faith.presentation.feature.quran.tilwah

import net.thechance.mena.faith.domain.model.Reciter

data class TilawahUiState(
    val reciters: List<ReciterUi> = emptyList(),
    val selectedReciterId: Int? = null,
    val selectedReciterForDelete: Int? = null,
    val showDeleteConfirmationDialog: Boolean = false,
    val isSelectedShown: Boolean = false,
    val isSwipeable: Boolean = false
) {
    data class ReciterUi(
        val id: Int,
        val name: String,
        val recitingType: String,
        val isDownloaded: Boolean,
    )
}

fun Reciter.toUi() = TilawahUiState.ReciterUi(
    id = id,
    name = name,
    recitingType = tilawahType,
    isDownloaded = true //Todo not implemented yet
)

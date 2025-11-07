package net.thechance.mena.faith.presentation.feature.quran.tilwah

import net.thechance.mena.faith.domain.model.Reciter

data class TilawahUiState(
    val reciters: List<ReciterUi> = emptyList(),
    val selectedReciterId: Int? = null,
    )

data class ReciterUi(
    val id: Int,
    val name: String,
    val recitingType: String,
    val isDownloaded: Boolean,
)

fun Reciter.toUi() = ReciterUi(
    id = id,
    name = name,
    recitingType = tilawahType,
    isDownloaded = true //Todo not implemented yet
)
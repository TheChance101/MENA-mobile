package net.thechance.mena.faith.presentation.feature.quran.reciter.surahRecitersScreen

import net.thechance.mena.faith.domain.model.Reciter

data class SurahRecitersUiState(
    val surahId: Int? = null,
    val reciters: List<ReciterUi> = emptyList(),
    val query: String = "",
    val queryHint: String = "",
    val selectedReciterId: Int? = null,
) {
    data class ReciterUi(
        val id: Int,
        val name: String,
        val recitingType: String,
        val isDownloaded: Boolean,
    )
}

fun Reciter.toUi(isDownloaded: Boolean) = SurahRecitersUiState.ReciterUi(
    id = id,
    name = name,
    recitingType = tilawahType,
    isDownloaded = isDownloaded
)
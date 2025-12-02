package net.thechance.mena.faith.presentation.feature.quran.reciter.reciterSelection

import net.thechance.mena.faith.domain.model.Reciter


data class RecitersSelectionUiState(
    val query: String = "",
    val queryHint: String = "",
    val lastSearchedQuery: String = "",
    val searchResults: List<ReciterSearchItemUi> = emptyList(),
    val selectedReciterId: Int? = null,
    )

data class ReciterSearchItemUi(
    val id: Int,
    val name: String,
    val recitingType: String,
)

fun Reciter.toUi() = ReciterSearchItemUi(
    id = id,
    name = name,
    recitingType = tilawahType,
)
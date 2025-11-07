package net.thechance.mena.faith.presentation.feature.quran.reciter

import net.thechance.mena.faith.presentation.feature.quran.tilwah.TilawahUiState

data class ReciterSearchUiState(
    val query: String = "",
    val queryHint: String = "",
    val lastSearchedQuery: String = "",
    val searchResults: List<TilawahUiState.ReciterUi> = emptyList(),
)

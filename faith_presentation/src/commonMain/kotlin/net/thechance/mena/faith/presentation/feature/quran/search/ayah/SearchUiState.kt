package net.thechance.mena.faith.presentation.feature.quran.search.ayah

import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah

data class SearchUiState(
    val surahId: Int?,
    val surahName: String? = null,
    val query: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val queryHint: String = ""
) {
    data class SearchResult(
        val number: Int,
        val surahId: Int,
        val surahName: String,
        val content: String,
        val plainContent: String,
    )
}

fun Ayah.toSearchResults(surahName: String? = null) = SearchUiState.SearchResult(
    number = number,
    surahId = surahId,
    surahName = surahName ?: Surah.SurahOrder.entries[surahId - 1].name,
    content = content,
    plainContent = plainContent
)
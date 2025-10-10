package net.thechance.mena.faith.presentation.feature.quran.search

data class SearchScreenState(
    val surahId: Int?,
    val surahName: String?,
    val query: String = "",
    val searchResult: List<SearchResult> = emptyList(),
    val hint: String = ""
)

data class SearchResult(
    val number: Int,
    val surahId: Int,
    val surahName: String,
    val content: String,
    val plainContent: String,
)
package net.thechance.mena.faith.presentation.feature.quran.search

sealed interface SearchEffect {
    data object NavigateBack : SearchEffect
    data class NavigateBackWithAyahResult(val ayahNumber: Int?) : SearchEffect
    data class NavigateToSurah(val surahId: Int, val ayahId: Int, val surahName: String) :
        SearchEffect
}
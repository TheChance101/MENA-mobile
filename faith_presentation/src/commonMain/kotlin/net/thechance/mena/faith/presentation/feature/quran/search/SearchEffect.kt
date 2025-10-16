package net.thechance.mena.faith.presentation.feature.quran.search

sealed interface SearchEffect {
    data class NavigateBack(val ayahNumber: Int? = null) : SearchEffect
    data class NavigateToSurah(
        val surahId: Int,
        val ayahId: Int,
        val surahName: String
    ) : SearchEffect
}
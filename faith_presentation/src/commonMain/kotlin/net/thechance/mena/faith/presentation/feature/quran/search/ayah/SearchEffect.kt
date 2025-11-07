package net.thechance.mena.faith.presentation.feature.quran.search.ayah

sealed interface SearchEffect {
    data class NavigateBack(val ayahNumber: Int? = null) : SearchEffect
    data class NavigateToSurah(
        val surahId: Int,
        val ayahId: Int,
        val surahName: String
    ) : SearchEffect
}
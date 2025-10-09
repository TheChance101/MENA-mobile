package net.thechance.mena.faith.presentation.feature.quran.search

sealed interface SearchEffect {
    data object NavigateBack : SearchEffect
    data class NavigateToSurah(val ayahId: Int, val surahId: Int) : SearchEffect
}
package net.thechance.mena.faith.presentation.feature.main

sealed interface MainScreenEffect {
    data class NavigateToSurah(val surahId: Int, val surahName: String) : MainScreenEffect
    data object NavigateToQuran : MainScreenEffect
    data object NavigateToQiblah : MainScreenEffect
    data object NavigateToMosques : MainScreenEffect
}

package net.thechance.mena.faith.presentation.feature.main

sealed interface MainScreenEffect {
    data class NavigateToSurah(
        val surahId: Int,
        val surahName: String,
        val ayahNumber: Int
    ) : MainScreenEffect
    data object NavigateToAddressesScreen : MainScreenEffect
    data object NavigateToQuran : MainScreenEffect
    data object NavigateToQiblah : MainScreenEffect
    data object NavigateToMosques : MainScreenEffect
    data object NavigateToPrayerTime : MainScreenEffect
    data object NavigateToTilawah : MainScreenEffect
}

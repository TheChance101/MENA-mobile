package net.thechance.mena.faith.presentation.feature.quran.sur

sealed interface SurEffect {
    data object NavigateBack : SurEffect
    data object NavigateToBookmark : SurEffect
    data class NavigateToSurahDetails(val surahId: Int) : SurEffect
    data object NavigateToSearch : SurEffect
}

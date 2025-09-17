package net.thechance.mena.faith.presentation.feature.quran.sur

sealed interface SurEffect {
    data object NavigateToBack : SurEffect
    data object NavigateToBookmark : SurEffect
    data class NavigateToSurahDetails(val surahId: Int,val surahName: String) : SurEffect
}

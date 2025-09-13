package net.thechance.mena.faith.presentation.feature.quran.sur

sealed interface SurEffect {
    data object BackNavigation : SurEffect
    data object BookmarkNavigation : SurEffect
    data class SurahDetailsNavigation(val surahId: Int) : SurEffect
}

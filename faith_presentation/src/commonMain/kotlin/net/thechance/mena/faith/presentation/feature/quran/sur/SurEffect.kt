package net.thechance.mena.faith.presentation.feature.quran.sur

sealed interface SurEffect {
    data object BackNavigation : SurEffect
    data object BookMarkNavigation : SurEffect
    data class SurahDetailsNavigation(val surahId: Int) : SurEffect
}

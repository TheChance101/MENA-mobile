package net.thechance.mena.faith.presentation.feature.quran.reciter.reciterSelection

sealed interface ReciterSelectionEffect {
    data object NavigateBack : ReciterSelectionEffect
}
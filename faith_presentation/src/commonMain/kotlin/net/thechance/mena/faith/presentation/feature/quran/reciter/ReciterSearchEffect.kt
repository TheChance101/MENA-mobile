package net.thechance.mena.faith.presentation.feature.quran.reciter

sealed interface ReciterSearchEffect {
    data object NavigateBack : ReciterSearchEffect
}
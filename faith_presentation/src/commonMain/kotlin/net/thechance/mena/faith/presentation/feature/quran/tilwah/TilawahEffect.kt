package net.thechance.mena.faith.presentation.feature.quran.tilwah

sealed interface TilawahEffect {
    data object NavigateBack : TilawahEffect
    data object NavigateToSearch: TilawahEffect
}

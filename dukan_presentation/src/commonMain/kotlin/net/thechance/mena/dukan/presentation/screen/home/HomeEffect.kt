package net.thechance.mena.dukan.presentation.screen.home

sealed class HomeEffect {
    object NavigateToAddDukanScreen : HomeEffect()
    object NavigateToPendingDukanScreen : HomeEffect()
}
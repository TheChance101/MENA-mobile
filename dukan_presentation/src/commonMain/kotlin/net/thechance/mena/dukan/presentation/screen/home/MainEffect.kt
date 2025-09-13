package net.thechance.mena.dukan.presentation.screen.home

sealed class MainEffect {
    object NavigateToAddDukanScreen : MainEffect()
    object NavigateToPendingDukanScreen : MainEffect()
}
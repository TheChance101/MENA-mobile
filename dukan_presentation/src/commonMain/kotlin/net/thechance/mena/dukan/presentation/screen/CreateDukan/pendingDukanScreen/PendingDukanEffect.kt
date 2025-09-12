package net.thechance.mena.dukan.presentation.screen.createDukan.pendingDukanScreen

sealed class PendingDukanEffect {
    data object NavigateBack : PendingDukanEffect()
}
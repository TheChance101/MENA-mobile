package net.thechance.mena.trends.presentation.screen.update_interests

internal sealed interface UpdateInterestsScreenEffect {
    data object NavigateBack : UpdateInterestsScreenEffect
    data object NavigateToTrends : UpdateInterestsScreenEffect
}
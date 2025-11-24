package net.thechance.mena.trends.presentation.screen.user_trend

internal sealed interface UserTrendEffect {
    data object NavigateBack : UserTrendEffect
    data object NavigateToPublisherProfile : UserTrendEffect
}
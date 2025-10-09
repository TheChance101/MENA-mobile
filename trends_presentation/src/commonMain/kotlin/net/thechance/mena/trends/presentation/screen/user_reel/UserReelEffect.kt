package net.thechance.mena.trends.presentation.screen.user_reel

internal sealed interface UserReelEffect {
    data object NavigateBack : UserReelEffect
    data object NavigateToPublisherProfile : UserReelEffect
}
package net.thechance.mena.trends.presentation.screen.upload_reel

sealed interface UploadReelScreenEffect {
    data object NavigateBack : UploadReelScreenEffect
    data class NavigateToAddDescription(val reelId: String) : UploadReelScreenEffect
}
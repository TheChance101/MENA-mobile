package net.thechance.mena.trends.presentation.screen.upload_reel

import net.thechance.mena.trends.presentation.shared.base.UploadReelErrorState

sealed interface UploadReelScreenEffect {
    data object NavigateBack : UploadReelScreenEffect
    data class NavigateToAddDescription(val reelId: String) : UploadReelScreenEffect
    data class ShowErrorSnackbar(val errorState: UploadReelErrorState) : UploadReelScreenEffect
}
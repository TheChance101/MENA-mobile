package net.thechance.mena.trends.presentation.screen.upload_reel

import net.thechance.mena.trends.presentation.shared.base.ErrorState

sealed interface UploadReelScreenEffect {
    data object NavigateBack : UploadReelScreenEffect
    data class NavigateToAddDescription(val reelId: String) : UploadReelScreenEffect
    data class ShowErrorSnackbar(val errorState: ErrorState) : UploadReelScreenEffect
    data object LaunchFilePicker : UploadReelScreenEffect
}
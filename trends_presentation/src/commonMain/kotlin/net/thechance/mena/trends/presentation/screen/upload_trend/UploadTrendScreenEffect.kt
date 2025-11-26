package net.thechance.mena.trends.presentation.screen.upload_trend

import net.thechance.mena.trends.presentation.shared.base.ErrorState

sealed interface UploadTrendScreenEffect {
    data object NavigateBack : UploadTrendScreenEffect
    data class NavigateToAddDescription(val trendId: String) : UploadTrendScreenEffect
    data class ShowErrorSnackbar(val errorState: ErrorState) : UploadTrendScreenEffect
    data object LaunchFilePicker : UploadTrendScreenEffect
}
package net.thechance.mena.trends.presentation.screen.upload_trend

sealed interface UploadTrendsScreenEffect {
    data object NavigateBack : UploadTrendsScreenEffect
    data class NavigateToDescription(val id: String) : UploadTrendsScreenEffect
}
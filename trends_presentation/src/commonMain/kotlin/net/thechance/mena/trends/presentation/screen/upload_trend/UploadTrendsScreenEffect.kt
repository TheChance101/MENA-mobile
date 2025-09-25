package net.thechance.mena.trends.presentation.screen.upload_trend

sealed interface UploadTrendsScreenEffect {
    data class NavigateToDescription(val id: String = "") : UploadTrendsScreenEffect
    data object OpenFilePicker : UploadTrendsScreenEffect
}
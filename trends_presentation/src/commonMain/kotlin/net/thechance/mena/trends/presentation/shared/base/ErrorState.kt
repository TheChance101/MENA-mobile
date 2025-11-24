package net.thechance.mena.trends.presentation.shared.base

sealed class ErrorState {
    object NoInternet : ErrorState()
    data class RequestFailed(val message: String? = "Request failed") : ErrorState()
    object RequestTimeout : ErrorState()
}

sealed class UploadTrendErrorState : ErrorState() {
    object FileTooLarge : UploadTrendErrorState()
    object DurationTooLarge : UploadTrendErrorState()
}
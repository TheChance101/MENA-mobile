package net.thechance.mena.trends.presentation.shared.base

sealed class ErrorState {
    object NoInternet : ErrorState()
    data class RequestFailed(val message: String? = "Request failed") : ErrorState()
    object RequestTimeout : ErrorState()
}

sealed class UploadReelErrorState : ErrorState() {
    object FileTooLarge : UploadReelErrorState()
    object DurationTooLarge : UploadReelErrorState()
}
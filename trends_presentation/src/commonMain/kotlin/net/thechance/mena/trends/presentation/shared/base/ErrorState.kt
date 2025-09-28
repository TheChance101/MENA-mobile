package net.thechance.mena.trends.presentation.shared.base

sealed class ErrorState {
    object NoInternet : ErrorState()
    object RequestTimeout : ErrorState()
    data class RequestFailed(val message: String? = "Request failed") : ErrorState()
    object FileTooLarge : ErrorState()
    object DurationTooLarge : ErrorState()
}
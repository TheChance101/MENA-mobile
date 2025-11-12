package net.thechance.mena.wallet.presentation.base

sealed interface ErrorState {
    data object NoInternet : ErrorState
    data object NoDataFound : ErrorState
    data object UnknownError : ErrorState
    data object BlockedReceiver : ErrorState
}
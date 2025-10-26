package net.thechance.mena.admin_panel.presentation.base

sealed interface ErrorState {
    data object NoInternet : ErrorState
    data object UnknownError : ErrorState
}
package net.thechance.mena.admin_panel.presentation.base

interface ErrorState {
    data object NoInternet : ErrorState
    data object UnknownError : ErrorState
}
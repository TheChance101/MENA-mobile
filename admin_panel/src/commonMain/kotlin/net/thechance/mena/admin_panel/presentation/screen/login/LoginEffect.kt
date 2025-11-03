package net.thechance.mena.admin_panel.presentation.screen.login

sealed interface LoginEffect {
    object NavigateToAdminPanel : LoginEffect
}
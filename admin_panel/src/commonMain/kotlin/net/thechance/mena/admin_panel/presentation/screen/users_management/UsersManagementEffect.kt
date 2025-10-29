package net.thechance.mena.admin_panel.presentation.screen.users_management

sealed interface UsersManagementEffect {
    data object NavigateBack : UsersManagementEffect
    class ShowConfirmationMessage(val message: String) : UsersManagementEffect
}

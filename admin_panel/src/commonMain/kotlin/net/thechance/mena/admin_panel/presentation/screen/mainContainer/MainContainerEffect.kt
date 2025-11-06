package net.thechance.mena.admin_panel.presentation.screen.mainContainer

sealed interface MainContainerEffect {
    data object NavigateToLogInScreen : MainContainerEffect
    data object NavigateToAdminPanelScreen : MainContainerEffect
    data object NavigateToUsersManagementScreen : MainContainerEffect
    data object NavigateToDepositScreen : MainContainerEffect
    data object NavigateToDukanRequestsScreen : MainContainerEffect
    data object NavigateToDukanManagementScreen : MainContainerEffect
}
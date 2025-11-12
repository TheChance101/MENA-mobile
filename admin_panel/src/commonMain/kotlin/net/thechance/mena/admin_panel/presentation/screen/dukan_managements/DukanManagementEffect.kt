package net.thechance.mena.admin_panel.presentation.screen.dukan_managements

sealed interface DukanManagementEffect {
    data class NavigateToDukanDetails(val dukanId: String) : DukanManagementEffect
}
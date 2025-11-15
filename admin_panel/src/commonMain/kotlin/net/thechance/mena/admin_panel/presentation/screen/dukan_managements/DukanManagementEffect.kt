@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.admin_panel.presentation.screen.dukan_managements

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface DukanManagementEffect {
    data class NavigateToDukanDetails(val dukanId: Uuid) : DukanManagementEffect
}
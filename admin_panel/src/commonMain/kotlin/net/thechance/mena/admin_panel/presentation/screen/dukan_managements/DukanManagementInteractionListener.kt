package net.thechance.mena.admin_panel.presentation.screen.dukan_managements

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface DukanManagementInteractionListener {
    fun onSearchQueryChange(query: String)
    fun onClearQueryClicked()
    fun onRetryClicked()
    fun onSortClicked(type: DukanManagementScreenState.SortType)
    fun onToggleUserStatusClicked(dukanId: Uuid, dukanStatus: Boolean)
}
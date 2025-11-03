package net.thechance.mena.admin_panel.presentation.screen.users_management

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)

interface UsersManagementInteractionListener {
    fun onSortClicked(type: UsersManagementScreenState.SortType)
    fun onToggleUserStatusClicked(userId: Uuid)
    fun onRetryClicked()
    fun onSearchQueryChanged(query: String)
    fun onClearQueryClicked()
    fun showBlockDialog(userId: Uuid)
    fun onDismissBlockDialog()
    fun onConfirmBlock()
}

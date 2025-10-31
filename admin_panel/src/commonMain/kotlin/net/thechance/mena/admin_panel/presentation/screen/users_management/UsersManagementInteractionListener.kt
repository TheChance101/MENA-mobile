package net.thechance.mena.admin_panel.presentation.screen.users_management

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)

interface UsersManagementInteractionListener {
    fun onSortUsersNameClicked()
    fun onSortLastLoginDateClicked()
    fun onSortLastVisitDateClicked()
    fun onToggleUserStatusClicked(userId: Uuid)
    fun onRetryClicked()
    fun onStatusClicked(userId: Uuid)
    fun onSearchQueryChanged(query: String)
    fun onShowBlockDialog(userId: Uuid)
    fun onDismissBlockDialog()
    fun onConfirmBlock()
}

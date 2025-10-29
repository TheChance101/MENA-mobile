package net.thechance.mena.admin_panel.presentation.screen.users_management

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)

interface UsersManagementInteractionListener {
    fun onNavigateBackClicked()
    fun onSortUsersNameClicked()
    fun onSortLastLoginDateClicked()
    fun onSortLastVisitDateClicked()
    fun onBlockUserClicked(userId: Uuid)
    fun onActivateUserClicked(userId: Uuid)
    fun onRetryClicked()

    fun onStatusClick(userId: Uuid)
    fun onSearchQueryChanged(query: String)
    fun onShowBlockDialog(userId: Uuid)
    fun onDismissBlockDialog()
    fun onConfirmBlock()
}

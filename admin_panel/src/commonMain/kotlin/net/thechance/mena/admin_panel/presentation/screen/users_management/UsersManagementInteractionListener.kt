package net.thechance.mena.admin_panel.presentation.screen.users_management

import net.thechance.mena.admin_panel.domain.entity.user.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)

interface UsersManagementInteractionListener {
    fun onSortClicked(type: UsersManagementScreenState.SortType)
    fun onToggleUserStatusClicked(userId: Uuid, userStatus: User.Status)
    fun onRetryClicked()
    fun onSearchQueryChanged(query: String)
    fun onClearQueryClicked()
    fun onPageChanged(page: Int)
    fun onBlockDialogClicked(userId: Uuid)
    fun onBlockDialogDismissed()
    fun onBlockConfirmed()
}

@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.admin_panel.presentation.screen.users_management

import net.thechance.mena.admin_panel.domain.entity.user.Status
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import kotlinx.datetime.LocalDate
import net.thechance.mena.admin_panel.domain.model.SortDirection
import net.thechance.mena.admin_panel.domain.model.SortType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


data class UsersManagementScreenState(
    val users: List<UserItem> = emptyList(),
    val query: String = "",
    val sort: SortState = SortState(),
    val isLoading: Boolean = false,
    val errorState: ErrorState? = null,
    val showBlockDialog: Boolean = false,
    val selectedUserId: Uuid? = null
) {
    data class UserItem(
        val id: Uuid,
        val fullName: String,
        val phoneNumber: String,
        val lastLoginAt: LocalDate,
        val lastVisitAt: LocalDate,
        val status: Status
    )

    data class SortState(
        val type: SortType = SortType.NONE,
        val direction: SortDirection = SortDirection.ASC
    )

    enum class SortDirection {
        ASC, DESC;
       fun toggle(): SortDirection = if (this == ASC) DESC else ASC
    }

    enum class SortType {
        USERNAME, LAST_LOGIN_DATE, LAST_VISIT_DATE, NONE
    }
}
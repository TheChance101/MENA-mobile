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
    val userNameSort: Sort = Sort.NONE,
    val lastLoginDateSort: Sort = Sort.NONE,
    val lastVisitDateSort: Sort = Sort.NONE,
    val isUserActive: Boolean = true,
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

    enum class Sort {
        ASC,
        DESC,
        NONE
    }
}

fun UsersManagementScreenState.Sort.toDomain(): SortDirection? {
    return when (this) {
        UsersManagementScreenState.Sort.ASC -> SortDirection.ASC
        UsersManagementScreenState.Sort.DESC -> SortDirection.DESC
        UsersManagementScreenState.Sort.NONE -> null
    }
}

fun UsersManagementScreenState.getActiveSortType(): SortType? = when {
    userNameSort != UsersManagementScreenState.Sort.NONE -> SortType.USERNAME
    lastLoginDateSort != UsersManagementScreenState.Sort.NONE -> SortType.LAST_LOGIN_DATE
    lastVisitDateSort != UsersManagementScreenState.Sort.NONE -> SortType.LAST_VISIT_DATE
    else -> null
}

fun UsersManagementScreenState.getActiveSortDirection(): SortDirection? = when {
    userNameSort != UsersManagementScreenState.Sort.NONE -> userNameSort.toDomain()
    lastLoginDateSort != UsersManagementScreenState.Sort.NONE -> lastLoginDateSort.toDomain()
    lastVisitDateSort != UsersManagementScreenState.Sort.NONE -> lastVisitDateSort.toDomain()
    else -> null
}

fun UsersManagementScreenState.Sort.toggle(): UsersManagementScreenState.Sort = when (this) {
    UsersManagementScreenState.Sort.NONE -> UsersManagementScreenState.Sort.ASC
    UsersManagementScreenState.Sort.ASC -> UsersManagementScreenState.Sort.DESC
    UsersManagementScreenState.Sort.DESC -> UsersManagementScreenState.Sort.NONE
}
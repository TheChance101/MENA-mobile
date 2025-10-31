@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.admin_panel.presentation.screen.users_management

import net.thechance.mena.admin_panel.domain.entity.User.UserState
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import java.time.LocalDate
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
        val userName: String,
        val phoneNumber: String,
        val lastLoginDate: LocalDate,
        val lastVisitDate: LocalDate,
        val userStates: UserState
    )

    enum class Sort {
        ASC,
        DESC,
        NONE
    }
}
@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.admin_panel.presentation.screen.users_management

import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class UsersManagementScreenState(
    val users: List<UserItem> = emptyList(),
    val query: String = "",
    val pageInfo: UserPageInfo = UserPageInfo(),
    val sort: SortState = SortState(),
    val isLoading: Boolean = false,
    val isInitialLoading: Boolean = true,
    val errorState: ErrorState? = null,
    val snackBar: SnackBarState = SnackBarState(),
    val isBlockDialogShown: Boolean = false,
    val selectedUserId: Uuid? = null,
    val totalUsers : Int = 0,
) {
    val isSortingDisabled = totalUsers < 2

    data class UserPageInfo(
        val page: Int = 0,
        val totalPages: Int = 1
    )

    data class UserItem(
        val index: Int,
        val id: Uuid,
        val fullName: String,
        val phoneNumber: String,
        val lastLoginAt: String,
        val lastVisitAt: String,
        val status: User.Status
    )

    data class SortState(
        val type: SortType = SortType.USERNAME,
        val direction: SortDirection = SortDirection.ASC
    )

    enum class SortDirection {
        ASC, DESC;

        fun toggle(): SortDirection = if (this == ASC) DESC else ASC
    }

    enum class SortType {
        USERNAME,
        LAST_LOGIN_DATE, LAST_VISIT_DATE,
        ACTIVATION_STATUS
    }
}
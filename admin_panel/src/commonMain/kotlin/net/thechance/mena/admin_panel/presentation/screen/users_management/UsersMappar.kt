package net.thechance.mena.admin_panel.presentation.screen.users_management

import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.model.SortDirection
import net.thechance.mena.admin_panel.domain.model.SortType
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun User.toUIState(): UsersManagementScreenState.UserItem {
    return UsersManagementScreenState.UserItem(
        id = id,
        fullName = "$firstName $lastName",
        phoneNumber = phoneNumber,
        lastLoginAt = lastLoginAt,
        lastVisitAt = lastVisitAt,
        status = status
    )
}

fun UsersManagementScreenState.SortType.toEntity(): SortType? = when (this) {
    UsersManagementScreenState.SortType.USERNAME -> SortType.USERNAME
    UsersManagementScreenState.SortType.LAST_LOGIN_DATE -> SortType.LAST_LOGIN_DATE
    UsersManagementScreenState.SortType.LAST_VISIT_DATE -> SortType.LAST_VISIT_DATE
    UsersManagementScreenState.SortType.NONE ->  null
}

fun UsersManagementScreenState.SortDirection.toEntity(): SortDirection? = when (this) {
    UsersManagementScreenState.SortDirection.ASC -> SortDirection.ASC
    UsersManagementScreenState.SortDirection.DESC -> SortDirection.DESC
}
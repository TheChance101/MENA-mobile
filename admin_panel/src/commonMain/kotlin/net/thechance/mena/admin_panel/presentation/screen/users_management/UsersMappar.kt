package net.thechance.mena.admin_panel.presentation.screen.users_management

import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.model.SortDirection
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
fun UsersManagementScreenState.Sort.toDomain(): SortDirection? {
    return when (this) {
        UsersManagementScreenState.Sort.ASC -> SortDirection.ASC
        UsersManagementScreenState.Sort.DESC -> SortDirection.DESC
        UsersManagementScreenState.Sort.NONE -> null
    }
}

package net.thechance.mena.admin_panel.presentation.screen.users_management

import net.thechance.mena.admin_panel.domain.entity.user.User
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

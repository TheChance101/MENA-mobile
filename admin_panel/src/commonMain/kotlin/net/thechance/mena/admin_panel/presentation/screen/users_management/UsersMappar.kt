package net.thechance.mena.admin_panel.presentation.screen.users_management

import net.thechance.mena.admin_panel.domain.entity.User
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun User.toUIState(): UsersManagementScreenState.UserItem {
    return UsersManagementScreenState.UserItem(
        id = id,
        userName = userName,
        phoneNumber = phoneNumber,
        lastLoginDate = lastLoginDate,
        lastVisitDate = lastVisitDate,
        userStates = userState
    )
}

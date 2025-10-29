@file:OptIn(ExperimentalUuidApi::class)


package net.thechance.mena.admin_panel.domain.entity

import net.thechance.mena.admin_panel.presentation.screen.users_management.UsersManagementScreenState
import java.time.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class User(
    val id: Uuid,
    val userName: String,
    val phoneNumber: String,
    val lastLoginDate: LocalDate,
    val lastVisitDate: LocalDate,
    val userStates: UserStates
)

enum class UserStates {
    ACTIVE, BLOCKED
}


fun User.toUIState(): UsersManagementScreenState.UserItem {
    return UsersManagementScreenState.UserItem(
        id = id,
        userName = userName,
        phoneNumber = phoneNumber,
        lastLoginDate = lastLoginDate,
        lastVisitDate = lastVisitDate,
        userStates = userStates
    )
}

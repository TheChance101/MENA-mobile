@file:OptIn(ExperimentalUuidApi::class)


package net.thechance.mena.admin_panel.domain.entity

import java.time.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class User(
    val id: Uuid,
    val userName: String,
    val phoneNumber: String,
    val lastLoginDate: LocalDate,
    val lastVisitDate: LocalDate,
    val userState: UserState
) {
    enum class UserState {
        ACTIVE, BLOCKED
    }
}
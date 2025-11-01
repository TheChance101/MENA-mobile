@file:OptIn(ExperimentalUuidApi::class)


package net.thechance.mena.admin_panel.domain.entity

import java.time.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class User(
    val id: Uuid,
    val fullName: String,
    val phoneNumber: String,
    val lastLoginDate: LocalDate,
    val lastVisitDate: LocalDate,
    val userStates: UserStates
) {
    enum class UserStates {
        ACTIVE, BLOCKED
    }
}
@file:OptIn(ExperimentalUuidApi::class)
package net.thechance.mena.admin_panel.domain.entity.user

import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class User(
    val id: Uuid,
    val phoneNumber : String,
    val firstName: String,
    val lastName: String,
    val lastLoginAt: LocalDate,
    val lastVisitAt: LocalDate,
    val status: Status
)
enum class Status {
    ACTIVE,
    BLOCKED;
    companion object {
        fun valueOfOrDefault(value: String?): Status {
            return runCatching { value?.let { Status.valueOf(it) } ?: ACTIVE }.getOrDefault (ACTIVE)
        }
    }
}
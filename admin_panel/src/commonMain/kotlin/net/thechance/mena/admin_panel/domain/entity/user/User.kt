@file:OptIn(ExperimentalUuidApi::class)
package net.thechance.mena.admin_panel.domain.entity.user

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class User(
    val id: Uuid,
    val phoneNumber : String,
    val firstName: String,
    val lastName: String,
    val lastLoginAt: LocalDateTime,
    val lastVisitAt: LocalDateTime,
    val status: Status
) {
    enum class Status {
        ACTIVE,
        BLOCKED;

        companion object {
            fun valueOfOrDefault(value: String?): Status {
                return runCatching { value?.let { Status.valueOf(it) } ?: ACTIVE }.getOrDefault(
                    ACTIVE
                )
            }
        }
    }
}
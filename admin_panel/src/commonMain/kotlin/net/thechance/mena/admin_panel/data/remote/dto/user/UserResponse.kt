package net.thechance.mena.admin_panel.data.remote.dto.user

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.admin_panel.domain.entity.user.Status
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val firstName: String,
    val phoneNumber:String,
    val lastName: String,
    val lastLoginAt: LocalDateTime,
    val lastVisitAt: LocalDateTime,
    val status: Status
)
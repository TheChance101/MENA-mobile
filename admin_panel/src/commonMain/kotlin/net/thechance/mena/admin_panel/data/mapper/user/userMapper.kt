package net.thechance.mena.admin_panel.data.mapper.user

import net.thechance.mena.admin_panel.data.mapper.parseLocalDateTimeOrDefault
import net.thechance.mena.admin_panel.data.mapper.toUuidOrNull
import net.thechance.mena.admin_panel.data.remote.dto.user.UserResponse
import net.thechance.mena.admin_panel.domain.entity.user.User
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun UserResponse.toEntity(): User {
    return User(
        id = id.toUuidOrNull() ?: throw IllegalStateException("Invalid User id"),
        phoneNumber = phoneNumber.orEmpty(),
        lastLoginAt = parseLocalDateTimeOrDefault(lastLoginAt),
        lastVisitAt = parseLocalDateTimeOrDefault(lastVisitAt),
        status = User.Status.valueOfOrDefault(status),
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
    )
}

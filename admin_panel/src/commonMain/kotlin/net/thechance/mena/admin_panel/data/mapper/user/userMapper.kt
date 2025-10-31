package net.thechance.mena.admin_panel.data.mapper.user

import net.thechance.mena.admin_panel.data.mapper.parseLocalDateOrDefault
import net.thechance.mena.admin_panel.data.mapper.toUuidOrNull
import net.thechance.mena.admin_panel.data.remote.dto.user.UserResponse
import net.thechance.mena.admin_panel.domain.entity.user.Status
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.exceptions.UnknownNetworkException
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun UserResponse.toEntity(): User {
    return User(
        id = id.toUuidOrNull() ?: throw UnknownNetworkException("Invalid User id"),
        phoneNumber = phoneNumber ?: "",
        lastLoginAt = parseLocalDateOrDefault(lastLoginAt),
        lastVisitAt = parseLocalDateOrDefault(lastVisitAt),
        status = Status.valueOfOrDefault(status),
        firstName = firstName ?: "",
        lastName = lastName ?: "",
    )
}
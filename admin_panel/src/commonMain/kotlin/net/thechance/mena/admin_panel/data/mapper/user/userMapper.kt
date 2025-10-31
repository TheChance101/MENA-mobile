package net.thechance.mena.admin_panel.data.mapper.user


import kotlinx.datetime.toLocalDate
import net.thechance.mena.admin_panel.data.mapper.toUuidOrNull
import net.thechance.mena.admin_panel.data.remote.dto.user.UserResponse
import net.thechance.mena.admin_panel.domain.entity.user.Status
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.exceptions.UnknownNetworkException
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun UserResponse.toDomain(): User {
    return User(
        id=id.toUuidOrNull()?: throw UnknownNetworkException("Invalid User id"),
        phoneNumber=phoneNumber,
        lastLoginAt=lastLoginAt.toLocalDate(),
        lastVisitAt = lastVisitAt.toLocalDate(),
        status = Status.valueOfOrDefault(status),
        firstName = firstName,
        lastName = lastName,

    )

}
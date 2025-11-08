package net.thechance.mena.identity.data.mapper

import net.thechance.mena.identity.data.dataSource.local.database.model.UserEntity
import net.thechance.mena.identity.data.dto.auth.request.RegisterRequestDto
import net.thechance.mena.identity.data.dto.auth.response.AuthenticationResponse
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.model.RegisterRequest

fun RegisterRequest.toDto(sessionId: String) = RegisterRequestDto(
    phoneNumber = phoneNumber.getFormattedPhoneNumber(),
    username = username,
    firstName = firstName,
    lastName = lastName,
    birthDate = birthDate.toString(),
    gender = gender.toInt(),
    password = password,
    sessionId = sessionId
)

fun AuthenticationResponse.toDomain() = AuthenticationTokens(
    accessToken = accessToken,
    refreshToken = refreshToken
)

private fun Gender.toInt() = when (this) {
    Gender.MALE -> UserEntity.MALE
    Gender.FEMALE -> UserEntity.FEMALE
}
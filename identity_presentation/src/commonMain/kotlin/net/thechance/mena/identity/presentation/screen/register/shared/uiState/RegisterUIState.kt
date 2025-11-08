package net.thechance.mena.identity.presentation.screen.register.shared.uiState

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.RegisterRequest
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class RegisterUIState @OptIn(ExperimentalTime::class) constructor(
    val phoneNumber: PhoneNumber = PhoneNumber(countryCode = "", localNumber = "00"),
    val countryCode: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val password: String = "",
    val birthDate: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
)

fun RegisterUIState.toRegisterRequest(gender: Gender) = RegisterRequest(
    phoneNumber = phoneNumber,
    username = username,
    firstName = firstName,
    lastName = lastName,
    birthDate = birthDate,
    gender = gender,
    password = password
)
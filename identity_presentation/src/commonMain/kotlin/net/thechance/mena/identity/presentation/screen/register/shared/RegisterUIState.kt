package net.thechance.mena.identity.presentation.screen.register.shared

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.model.RegisterRequest
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
@OptIn(ExperimentalTime::class)
data class RegisterUIState (
    val phoneNumber: PhoneNumberUIState = PhoneNumberUIState(countryCode = "", localNumber = "00"),
    val countryCode: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val password: String = "",
    val birthDate: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
)

fun RegisterUIState.toRegisterRequest(gender: Gender):RegisterRequest {
    return RegisterRequest(
        phoneNumber = phoneNumber.toPhoneNumber(),
        username = username,
        firstName = firstName,
        lastName = lastName,
        birthDate = birthDate,
        gender = gender,
        password = password
    )
}

fun RegisterUIState.toRegisterJsonString(): String{
    return Json.encodeToString(this)
}

fun convertJsonStringToRegisterUIState(jsonString:String):RegisterUIState{
    return Json.decodeFromString(jsonString)
}
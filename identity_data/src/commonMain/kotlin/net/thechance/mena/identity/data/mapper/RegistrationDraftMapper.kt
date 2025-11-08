package net.thechance.mena.identity.data.mapper

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.serialization.Serializable
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.RegistrationDraft

@Serializable
internal data class RegistrationDraftDto(
    val countryCode: String? = null,
    val localNumber: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val password: String = "",
    val birthDateYear: Int? = null,
    val birthDateMonth: Int? = null,
    val birthDateDay: Int? = null,
    val gender: String? = null
)

internal fun RegistrationDraftDto.toDomain() = RegistrationDraft(
    phoneNumber = createPhoneNumber(),
    firstName = firstName,
    lastName = lastName,
    username = username,
    password = password,
    birthDate = createBirthDate(),
    gender = gender?.let(Gender::valueOf)
)

internal fun RegistrationDraft.toDto(phoneNumber: PhoneNumber) = RegistrationDraftDto(
    countryCode = getCountryCode(phoneNumber),
    localNumber = getLocalNumber(phoneNumber),
    firstName = firstName,
    lastName = lastName,
    username = username,
    password = password,
    birthDateYear = birthDate?.year,
    birthDateMonth = birthDate?.month?.number,
    birthDateDay = birthDate?.day,
    gender = gender?.name
)

private fun RegistrationDraft.getCountryCode(fallback: PhoneNumber) =
    phoneNumber?.countryCode ?: fallback.countryCode

private fun RegistrationDraft.getLocalNumber(fallback: PhoneNumber) =
    phoneNumber?.localNumber ?: fallback.localNumber

private fun RegistrationDraftDto.createPhoneNumber(): PhoneNumber? =
    countryCode?.let { code ->
        localNumber?.let { number ->
            PhoneNumber(code, number)
        }
    }

private fun RegistrationDraftDto.createBirthDate(): LocalDate? =
    createBirthDate(birthDateYear, birthDateMonth, birthDateDay)

private fun createBirthDate(year: Int?, month: Int?, day: Int?): LocalDate? =
    allNotNull(year, month, day)?.let { (y, m, d) ->
        LocalDate(y, m, d)
    }

private fun allNotNull(year: Int?, month: Int?, day: Int?): Triple<Int, Int, Int>? =
    if (year != null && month != null && day != null) {
        Triple(year, month, day)
    } else {
        null
    }
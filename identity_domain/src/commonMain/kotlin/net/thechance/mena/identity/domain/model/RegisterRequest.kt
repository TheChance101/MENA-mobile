package net.thechance.mena.identity.domain.model

import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.PhoneNumber

data class RegisterRequest(
    val phoneNumber: PhoneNumber,
    val username: String,
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val gender: Gender,
    val password: String
)
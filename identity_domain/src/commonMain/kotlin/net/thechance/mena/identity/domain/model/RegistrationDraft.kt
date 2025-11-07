package net.thechance.mena.identity.domain.model

import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.PhoneNumber

data class RegistrationDraft(
    val phoneNumber: PhoneNumber? = null,
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val password: String = "",
    val birthDate: LocalDate? = null,
    val gender: Gender? = null
)
package net.thechance.mena.identity.domain.entity

import kotlinx.datetime.LocalDate

data class User(
    val firstName: String,
    val lastName: String,
    val profileImageUrl: String,
    val username: String,
    val birthDate: LocalDate,
    val gender: Gender
)

enum class Gender {
    MALE,
    FEMALE,
}
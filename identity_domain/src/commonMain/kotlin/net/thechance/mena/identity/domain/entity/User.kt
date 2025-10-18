package net.thechance.mena.identity.domain.entity

import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class User @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
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
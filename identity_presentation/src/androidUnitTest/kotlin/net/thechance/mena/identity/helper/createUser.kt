package net.thechance.mena.identity.helper

import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun createUser(
    id: Uuid = Uuid.parse("1bfbf5d8-145d-40e9-abae-8335df3f0a81"),
    firstName: String = "The ",
    lastName: String = "Chance",
    username: String = "the-chance",
    profileImageUrl: String = "",
    birthDate: LocalDate = LocalDate(1900, 1, 1),
    gender: Gender = Gender.MALE,
) = User(
    id = id,
    firstName = firstName,
    lastName = lastName,
    username = username,
    profileImageUrl = profileImageUrl,
    birthDate = birthDate,
    gender = gender,
)
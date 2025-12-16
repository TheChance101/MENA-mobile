package net.thechance.mena.identity.presentation.screen.profile.shared

import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.Gender
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class UserUIState @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
    val firstName: String,
    val lastName: String,
    val profileImageUrl: String,
    val username: String,
    val birthDate: LocalDate,
    val gender: Gender
)
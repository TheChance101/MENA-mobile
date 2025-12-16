package net.thechance.mena.identity.presentation.screen.profile.shared

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.util.orCurrentDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@OptIn(ExperimentalUuidApi::class)
data class UserUIState (
    val id: Uuid = Uuid.random(),
    val firstName: String = "",
    val lastName: String = "",
    val profileImageUrl: String = "",
    val username: String = "",
    val birthDate: LocalDate? = null,
    val gender: Gender = Gender.MALE
)

@OptIn(ExperimentalUuidApi::class)
fun User.toUserUIState():UserUIState{
    return UserUIState(
        id = id,
        firstName = firstName,
        lastName = lastName,
        profileImageUrl = profileImageUrl,
        username = username,
        birthDate = birthDate,
        gender = gender
    )
}

@OptIn(ExperimentalUuidApi::class)
fun UserUIState.toUser():User{
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        profileImageUrl = profileImageUrl,
        username = username,
        birthDate = birthDate.orCurrentDate(),
        gender = gender
    )
}

fun UserUIState.toJsonString():String{
    return Json.encodeToString(this)
}

fun convertJsonStringToUserUIState(jsonString:String):UserUIState{
    return Json.decodeFromString(jsonString)
}
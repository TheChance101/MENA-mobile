package net.thechance.mena.identity.presentation.screen.editProfile

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class EditUserProfileUIState(
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val profileImageUrl: String = "",
    val profileImageBitmap: ImageBitmap? = null,
    val birthDate: LocalDate? = null,
    val gender: Gender = Gender.MALE,
    val showEditImageDialog: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showConfirmLogoutDialog: Boolean = false,
    val showConfirmDeleteAccountDialog: Boolean = false,
    val showCamera: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val profileImageAction: ProfileImageAction = ProfileImageAction.NONE,
) {
    enum class ProfileImageAction {
        UPDATE,
        DELETE,
        NONE
    }
}

data class UserUIState(
    val id: String,
    val firstName: String,
    val lastName: String,
    val profileImageUrl: String,
    val username: String,
    val birthDate: String,
    val gender: Gender
)

@OptIn(ExperimentalUuidApi::class)
fun UserUIState.toUser(): User {
    return User(
        id = Uuid.parse(id),
        username = username,
        firstName = firstName,
        lastName = lastName,
        profileImageUrl = profileImageUrl,
        birthDate = LocalDate.parse(birthDate),
        gender = gender
    )
}
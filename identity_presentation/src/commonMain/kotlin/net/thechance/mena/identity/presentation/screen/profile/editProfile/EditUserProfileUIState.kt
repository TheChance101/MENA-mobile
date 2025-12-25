package net.thechance.mena.identity.presentation.screen.profile.editProfile

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.Gender

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

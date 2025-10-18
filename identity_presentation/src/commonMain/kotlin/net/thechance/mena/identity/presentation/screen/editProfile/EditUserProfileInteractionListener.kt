package net.thechance.mena.identity.presentation.screen.editProfile

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface EditUserProfileInteractionListener : BaseInteractionListener {
    fun onChangeFirstName(firstName: String)
    fun onChangeLastName(lastName: String)
    fun onChangeUsername(username: String)
    fun onChangeGender(gender: Gender)
    fun onClickSaveButton()
    fun onClickCancelButton()
    fun onClickShowLogoutOptions()
    fun onChangeDate(day: Int, month: Int, year: Int)
    fun clearErrorMessage()
    fun onClickEditImage()
    fun onDismissEditImageDialog()
    fun onDismissLogoutDialog()
    fun onRemoveProfileImage()
    fun onRequireCropImage(imageBitmap: ImageBitmap)
    fun onTakeImageFromCamera()
    fun onOpenCamera()
}
package net.thechance.mena.identity.presentation.screen.register.uploadProfileImage

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.identity.presentation.base.BaseInteractionListener

interface UploadProfileImageInteractionListener: BaseInteractionListener {
    fun onClickUpload()
    fun onClickSkip()
    fun onSelectImage(imageBitmap: ImageBitmap)
    fun onClickEdit(imageBitmap: ImageBitmap)
    fun onImageCropped(croppedImageBitmap: ImageBitmap)
}
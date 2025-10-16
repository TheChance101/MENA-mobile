package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.identity.presentation.base.BaseScreenModel

class ImageCropperViewModel(
    imageBitmap: ImageBitmap
) : BaseScreenModel<ImageCropperScreenState, ImageCropperScreenEffect>(
    initialState = ImageCropperScreenState(imageBitmap)
), ImageCropperInteractionListener {

    override fun onCropImage(imageBitmap: ImageBitmap) {
        sendNewEffect(ImageCropperScreenEffect.NavigateBackToEditProfileWithImage(imageBitmap))
    }

    override fun onChangeImage(imageBitmap: ImageBitmap) {
        updateState { copy(imageBitmap = imageBitmap) }
    }

    override fun onNavigateBack() {
        sendNewEffect(ImageCropperScreenEffect.NavigateBackToEditProfile)
    }
}
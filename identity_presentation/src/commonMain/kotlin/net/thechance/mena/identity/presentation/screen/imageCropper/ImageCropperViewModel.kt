package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.utils.ImageCacheManager

class ImageCropperViewModel(
    private val imageKey: String,
    imageCachedController: ImageCacheManager
) : BaseScreenModel<ImageCropperScreenState, ImageCropperScreenEffect>(
    initialState = ImageCropperScreenState()
), ImageCropperInteractionListener {

    init {
        val imageBitmap = imageCachedController.getCachedImage(imageKey)
        updateState { copy(imageBitmap = imageBitmap) }
    }
    override fun onCropImage(imageBitmap: ImageBitmap) {
        sendNewEffect(ImageCropperScreenEffect.NavigateBackToEditProfileWithImage(imageKey))
    }

    override fun onChangeImage(imageBitmap: ImageBitmap) {
        updateState { copy(imageBitmap = imageBitmap) }
    }

    override fun onNavigateBack() {
        sendNewEffect(ImageCropperScreenEffect.NavigateBackToEditProfile)
    }
}
package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.domain.repository.CachedImageRepository
import org.jetbrains.compose.resources.decodeToImageBitmap

class ImageCropperViewModel(
    private val imageKey: String,
    private val cachedImageRepository: CachedImageRepository
) : BaseScreenModel<ImageCropperScreenState, ImageCropperScreenEffect>(
    initialState = ImageCropperScreenState()
), ImageCropperInteractionListener {

    init {
        val imageBitmap = cachedImageRepository.getCachedImage(imageKey)
        updateState { copy(imageBitmap = imageBitmap?.decodeToImageBitmap()) }
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
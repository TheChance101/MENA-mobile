package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.graphics.ImageBitmap
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.image_crop_failed
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.domain.repository.ImagesRepository
import net.thechance.mena.identity.presentation.utils.ImageDecoder

class ImageCropperViewModel(
    private val imageKey: String,
    private val imagesRepository: ImagesRepository,
    private val imageDecoder: ImageDecoder
) : BaseScreenModel<ImageCropperScreenState, ImageCropperScreenEffect>(
    initialState = ImageCropperScreenState()
), ImageCropperInteractionListener {

    init {
        val imageByteArray = imagesRepository.getCachedImage(imageKey)
        updateState { copy(imageBitmap = imageByteArray?.let { imageDecoder.decodeImage(it) }) }
    }

    override fun onCropImage(imageBitmap: ImageBitmap) {
        tryToExecute(
            function = { encodeAndCacheImage(imageBitmap) },
            onSuccess = ::onCropImageSuccess,
            onError = ::handleCropImageException
        )
    }

    override fun onChangeImage(imageBitmap: ImageBitmap) {
        updateState { copy(imageBitmap = imageBitmap) }
    }

    override fun onNavigateBack() {
        sendNewEffect(ImageCropperScreenEffect.NavigateBackToEditProfile)
    }

    private suspend fun encodeAndCacheImage(imageBitmap: ImageBitmap) {
        val imageByteArray = imageDecoder.encodeImage(imageBitmap)
        imagesRepository.cacheImage(imageKey, imageByteArray)
    }

    private fun onCropImageSuccess(response: Unit) {
        sendNewEffect(ImageCropperScreenEffect.NavigateBackToEditProfileWithImage(imageKey))
    }

    private fun handleCropImageException(throwable: Throwable) {
        throwable.printStackTrace()
        updateState { copy(errorMessage = Res.string.image_crop_failed) }
    }
}
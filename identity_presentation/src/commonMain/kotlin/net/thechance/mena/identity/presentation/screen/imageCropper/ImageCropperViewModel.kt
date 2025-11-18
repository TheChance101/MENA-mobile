package net.thechance.mena.identity.presentation.screen.imageCropper

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.image_crop_failed
import net.thechance.mena.identity.domain.repository.ImagesRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel

class ImageCropperViewModel(
    private val imageKey: String,
    private val imagesRepository: ImagesRepository
) : BaseScreenModel<ImageCropperScreenState, ImageCropperScreenEffect>(
    initialState = ImageCropperScreenState()
), ImageCropperInteractionListener {

    init {
        val imageByteArray = imagesRepository.getCachedImage(imageKey)
        updateState { copy(imageByteArray = imageByteArray) }
    }

    override fun onCropImage(imageByteArray: ByteArray) {
        tryToExecute(
            function = { encodeAndCacheImage(imageByteArray) },
            onSuccess = ::onCropImageSuccess,
            onError = ::handleCropImageException
        )
    }

    override fun onChangeImage(imageByteArray: ByteArray) {
        updateState { copy(imageByteArray = imageByteArray) }
    }

    override fun onNavigateBack() {
        sendNewEffect(ImageCropperScreenEffect.NavigateBackToEditProfile)
    }

    override fun onDismissSnackBar() {
        updateState { copy(errorMessage = null) }
    }

    private fun encodeAndCacheImage(imageByteArray: ByteArray) {
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
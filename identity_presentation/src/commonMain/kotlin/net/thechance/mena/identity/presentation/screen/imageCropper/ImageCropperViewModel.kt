package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.domain.repository.CachedImageRepository
import net.thechance.mena.identity.presentation.utils.ImageDecoder

class ImageCropperViewModel(
    private val imageKey: String,
    private val cachedImageRepository: CachedImageRepository,
    private val imageDecoder: ImageDecoder
) : BaseScreenModel<ImageCropperScreenState, ImageCropperScreenEffect>(
    initialState = ImageCropperScreenState()
), ImageCropperInteractionListener {

    init {
        val imageByteArray = cachedImageRepository.getCachedImage(imageKey)
        updateState { copy(imageBitmap = imageByteArray?.let{ imageDecoder.decodeImage(it)}) }
    }
    override fun onCropImage(imageBitmap: ImageBitmap) {
        tryToExecute(
            function = {
                val imageByteArray = imageDecoder.encodeImage(imageBitmap)
                cachedImageRepository.cacheImage(imageKey, imageByteArray)
            },
            onSuccess = {
                sendNewEffect(ImageCropperScreenEffect.NavigateBackToEditProfileWithImage(imageKey))
            },
            onError = {
                it.printStackTrace()
                sendNewEffect(ImageCropperScreenEffect.NavigateBackToEditProfileWithImage(imageKey))
            }
        )
    }

    override fun onChangeImage(imageBitmap: ImageBitmap) {
        updateState { copy(imageBitmap = imageBitmap) }
    }

    override fun onNavigateBack() {
        sendNewEffect(ImageCropperScreenEffect.NavigateBackToEditProfile)
    }
}
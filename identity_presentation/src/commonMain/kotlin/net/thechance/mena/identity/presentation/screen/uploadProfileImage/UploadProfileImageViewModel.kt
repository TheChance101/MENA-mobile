package net.thechance.mena.identity.presentation.screen.uploadProfileImage

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.repository.ImagesRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.error.ErrorState
import net.thechance.mena.identity.presentation.base.error.handleAuthenticationException
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.utils.ImageDecoder
import org.jetbrains.compose.resources.StringResource

class UploadProfileImageViewModel(
    val imagesRepository: ImagesRepository,
    private val imageDecoder: ImageDecoder,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseScreenModel<UploadProfileImageUIState, UploadProfileImageUIEffect>
    (UploadProfileImageUIState()),
    UploadProfileImageInteractionListener {

    override fun onClickUpload() {

        updateState { copy(isLoading = true, errorMessage = null) }

        tryToExecute(
            function = {

                delay(2000)
                true
            },
            onSuccess = ::onUploadSuccess,
            dispatcher = dispatcher
        )
    }

    override fun onClickSkip() {
        sendNewEffect(UploadProfileImageUIEffect.NavigateToNextScreenAfterSkip)
    }

    override fun onSelectImage(imageBitmap: ImageBitmap) {
        updateState {
            copy(
                imageBitmap = imageBitmap,
                isUploadEnabled = true
            )
        }
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    private fun onUploadSuccess(result: Boolean) {
        updateState { copy(isLoading = false) }
        sendNewEffect(UploadProfileImageUIEffect.NavigateToNextScreen)
    }

    override fun onImageCropped(croppedImageBitmap: ImageBitmap) {
        updateState { copy(imageBitmap = croppedImageBitmap, isUploadEnabled = true) }
    }

    private fun onUploadError(errorState: ErrorState) {
        updateState { copy(isLoading = false) }
    }

    override fun onClickEdit(imageBitmap: ImageBitmap) {
        cacheRequiredCropImage(imageBitmap)
    }

    private fun cacheRequiredCropImage(imageBitmap: ImageBitmap) {
        tryToExecute(
            function = {
                imagesRepository.cacheImage(
                    IMAGE_KEY,
                    imageDecoder.encodeImage(imageBitmap)
                )
            },
            onSuccess = { handleCacheImageSuccess() },
            onError = ::onCacheCropImageError,
            dispatcher = dispatcher
        )
    }

    private fun handleCacheImageSuccess() {
        sendNewEffect(
            UploadProfileImageUIEffect.NavigateToCropScreen(
                imageKey = IMAGE_KEY,
                onResult = { croppedImageKey ->
                    val imageByteArray = imagesRepository.getCachedImage(croppedImageKey)
                    updateState {
                        copy(
                            imageBitmap = imageByteArray?.let { imageDecoder.decodeImage(it) },
                            isUploadEnabled = true
                        )
                    }
                }
            )
        )
    }

    private fun onCacheCropImageError(throwable: Throwable) {
        updateState { copy(errorMessage = mapErrorMessage(throwable)) }
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is AuthenticationException -> mapAuthenticationErrorToMessage(
                handleAuthenticationException(throwable)
            )

            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }

    private companion object {
        const val IMAGE_KEY = "register"
    }
}
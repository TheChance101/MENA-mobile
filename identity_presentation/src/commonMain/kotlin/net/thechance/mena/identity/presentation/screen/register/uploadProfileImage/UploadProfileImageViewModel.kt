package net.thechance.mena.identity.presentation.screen.register.uploadProfileImage

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.ImagesRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.utils.ImageDecoder
import org.jetbrains.compose.resources.StringResource

class UploadProfileImageViewModel(
    private val cachedImageRepository: ImagesRepository,
    private val userRepository: UserRepository,
    private val imageDecoder: ImageDecoder,
    private val authenticationRepository: AuthenticationRepository,
    private val registrationDraftRepository: RegistrationDraftRepository,
    private val authTokens: AuthenticationTokens? = null,
    private val phoneNumber: PhoneNumber? = null,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseScreenModel<UploadProfileImageUIState, UploadProfileImageUIEffect>
    (UploadProfileImageUIState()),
    UploadProfileImageInteractionListener {

    init {
        loadSavedImage()
    }

    private fun loadSavedImage() {
        phoneNumber?.let { number ->
            loadCachedImage(number)
        }
    }

    private fun loadCachedImage(phoneNumber: PhoneNumber) {
        tryToExecute(
            function = { getCachedImageBytes(phoneNumber) },
            onSuccess = ::handleCachedImageLoaded,
            dispatcher = dispatcher
        )
    }

    private fun getCachedImageBytes(phoneNumber: PhoneNumber) =
        cachedImageRepository.getCachedImage(getImageKey(phoneNumber))

    private fun handleCachedImageLoaded(cachedImage: ByteArray?) {
        cachedImage?.let { imageBytes ->
            updateStateWithDecodedImage(imageDecoder.decodeImage(imageBytes))
        }
    }

    private fun updateStateWithDecodedImage(imageBitmap: ImageBitmap) {
        updateState {
            copy(
                imageBitmap = imageBitmap,
                isUploadEnabled = true,
                isImageLoaded = true
            )
        }
    }

    override fun onClickUpload() {
        val imageBitmap = state.value.imageBitmap ?: return
        val currentAuthTokens = authTokens ?: return

        startUpload(imageBitmap, currentAuthTokens)
    }

    private fun startUpload(imageBitmap: ImageBitmap, authTokens: AuthenticationTokens) {
        updateState { copy(isLoading = true) }
        tryToExecute(
            function = { uploadImage(imageBitmap, authTokens) },
            onSuccess = { onUploadSuccess() },
            onError = ::onUploadError,
            dispatcher = dispatcher
        )
    }

    private suspend fun uploadImage(imageBitmap: ImageBitmap, authTokens: AuthenticationTokens) =
        withTemporaryTokens(authTokens) {
            val imageByteArray = imageDecoder.encodeImage(imageBitmap)
            userRepository.uploadUserProfileImage(imageByteArray)
        }

    override fun onClickSkip() {
        authTokens?.let { tokens ->
            phoneNumber?.let { number ->
                handleSkipUpload(tokens, number)
            }
        }
    }

    private fun handleSkipUpload(authTokens: AuthenticationTokens, phoneNumber: PhoneNumber) {
        markImageUploadCompleted()
        clearCachedImageAfterUpload(phoneNumber)
        navigateToAccountCreated(authTokens)
    }

    override fun onSelectImage(imageBitmap: ImageBitmap) {
        updateStateWithSelectedImage(imageBitmap)
        saveImage(imageBitmap)
    }

    private fun updateStateWithSelectedImage(imageBitmap: ImageBitmap) {
        updateState {
            copy(
                imageBitmap = imageBitmap,
                isUploadEnabled = true
            )
        }
    }

    private fun onUploadSuccess() {
        updateState { copy(isLoading = false) }
        authTokens?.let { tokens ->
            phoneNumber?.let { number ->
                completeUploadFlow(tokens, number)
            }
        }
    }

    private fun completeUploadFlow(authTokens: AuthenticationTokens, phoneNumber: PhoneNumber) {
        markImageUploadCompleted()
        clearCachedImageAfterUpload(phoneNumber)
        navigateToAccountCreated(authTokens)
    }

    private fun navigateToAccountCreated(authTokens: AuthenticationTokens) {
        sendNewEffect(UploadProfileImageUIEffect.NavigateToAccountCreated(authTokens))
    }

    private fun markImageUploadCompleted() {
        tryToExecute(
            function = { registrationDraftRepository.setImageUploadCompleted(true) },
            dispatcher = dispatcher
        )
    }

    private fun clearCachedImageAfterUpload(phoneNumber: PhoneNumber) {
        screenModelScope.launch(dispatcher) {
            removeCachedImage(phoneNumber)
        }
    }

    private fun removeCachedImage(phoneNumber: PhoneNumber) {
        val imageKey = getImageKey(phoneNumber)
        cachedImageRepository.removeCachedImage(imageKey)
    }

    private fun onUploadError(throwable: Throwable) {
        updateState {
            copy(
                isLoading = false,
            )
        }
        sendNewEffect(
            UploadProfileImageUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
    }

    override fun onImageCropped(croppedImageBitmap: ImageBitmap) {
        updateStateWithSelectedImage(croppedImageBitmap)
        saveImage(croppedImageBitmap)
    }

    override fun onClickEdit(imageBitmap: ImageBitmap) {
        cacheRequiredCropImage(imageBitmap)
    }

    private fun cacheRequiredCropImage(imageBitmap: ImageBitmap) {
        phoneNumber?.let { number ->
            cacheImageForCrop(imageBitmap, number)
        }
    }

    private fun cacheImageForCrop(imageBitmap: ImageBitmap, phoneNumber: PhoneNumber) {
        val imageKey = getImageKey(phoneNumber)
        tryToExecute(
            function = { encodeAndCacheImage(imageBitmap, imageKey) },
            onSuccess = { handleCacheImageSuccess(imageKey) },
            onError = ::onCacheCropImageError,
            dispatcher = dispatcher
        )
    }

    private suspend fun encodeAndCacheImage(imageBitmap: ImageBitmap, imageKey: String) {
        val imageByteArray = imageDecoder.encodeImage(imageBitmap)
        cachedImageRepository.cacheImage(imageKey, imageByteArray)
    }

    private fun saveImage(imageBitmap: ImageBitmap) {
        phoneNumber?.let { number ->
            saveImageForPhoneNumber(imageBitmap, number)
        }
    }

    private fun saveImageForPhoneNumber(imageBitmap: ImageBitmap, phoneNumber: PhoneNumber) {
        tryToExecute(
            function = { encodeAndSaveImage(imageBitmap, phoneNumber) },
            dispatcher = dispatcher
        )
    }

    private suspend fun encodeAndSaveImage(imageBitmap: ImageBitmap, phoneNumber: PhoneNumber) {
        val imageKey = getImageKey(phoneNumber)
        val imageByteArray = imageDecoder.encodeImage(imageBitmap)
        cachedImageRepository.cacheImage(imageKey, imageByteArray)
    }

    private fun handleCacheImageSuccess(imageKey: String) {
        sendNewEffect(createCropScreenEffect(imageKey))
    }

    private fun createCropScreenEffect(imageKey: String) =
        UploadProfileImageUIEffect.NavigateToCropScreen(
            imageKey = imageKey,
            onResult = ::handleCroppedImageResult
        )

    private fun handleCroppedImageResult(croppedImageKey: String) {
        val imageByteArray = cachedImageRepository.getCachedImage(croppedImageKey)
        imageByteArray?.let { bytes ->
            val imageBitmap = imageDecoder.decodeImage(bytes)
            updateStateWithSelectedImage(imageBitmap)
            saveImage(imageBitmap)
        }
    }

    private fun onCacheCropImageError(throwable: Throwable) {
        sendNewEffect(
            UploadProfileImageUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
    }

    private suspend fun <T> withTemporaryTokens(
        authTokens: AuthenticationTokens,
        block: suspend () -> T
    ): T {
        authenticationRepository.saveAuthTokensWithoutEmit(authTokens)
        return block()
    }

    private fun getImageKey(phoneNumber: PhoneNumber) =
        "register_image_${phoneNumber.getFormattedPhoneNumber()}"

    private fun mapErrorMessage(throwable: Throwable): StringResource = when (throwable) {
        is AuthenticationException -> mapAuthenticationErrorToMessage(
            handleUploadProfileImageException(throwable)
        )

        else -> mapErrorToMessage(ErrorState.GenericError(throwable))
    }
}
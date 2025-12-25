package net.thechance.mena.identity.presentation.screen.profile.profileMainScreen.components.share

import androidx.compose.ui.platform.Clipboard
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.cant_save_qr_code
import mena.identity_presentation.generated.resources.gallery_permission_required
import net.thechance.mena.identity.domain.repository.ImagesRepository
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.presentation.screen.profile.profileMainScreen.components.share.utils.clipEntryOf
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import net.thechance.mena.identity.presentation.util.permissionHandler.Permissions
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

class ShareDialogViewModel(
    private val userRepository: UserRepository,
    private val imagesRepository: ImagesRepository,
    private val galleryPermissionHandler: PermissionHandler,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    initialState: ShareQrCodeUIState = ShareQrCodeUIState()
) : ViewModel(), ShareQrCodeInteractionListener {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ShareQrCodeUIEffect>()
    val effect = _effect.asSharedFlow().throttleFirst(500).mapNotNull { it }

    init {
        setUrlLinks()
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun setUrlLinks() {
        viewModelScope.launch {
            userRepository.getUser().collect { user ->
                val shareLinkUrl = "$SHARE_URL${user?.id}"
                updateState { copy(shareLinkUrl = shareLinkUrl) }
            }
        }
    }

    override fun onClickDownload(byteArray: ByteArray) {
        requestPermission(byteArray)
    }

    private fun requestPermission(byteArray: ByteArray) {
        tryToExecute(
            function = { galleryPermissionHandler.requestPermission(permission = Permissions.GALLERY_IMAGES) },
            onSuccess = { permissionState -> onPermissionSuccess(permissionState = permissionState, byteArray = byteArray) },
            onError = ::onPermissionError,
            dispatcher = dispatcher
        )
    }

    private suspend fun onPermissionSuccess(permissionState: PermissionState, byteArray: ByteArray) {
        when(permissionState) {
            PermissionState.GRANTED -> {
                saveImageToGallery(byteArray)
            }

            PermissionState.NOT_DETERMINED -> {
                sendNewEffect(ShareQrCodeUIEffect.ShowSnackBarError(Res.string.gallery_permission_required))
            }

            PermissionState.DENIED -> {
                sendNewEffect(ShareQrCodeUIEffect.ShowSnackBarError(Res.string.gallery_permission_required))
            }

            PermissionState.DENIED_PERMANENTLY -> {
                galleryPermissionHandler.openSettingPage(Permissions.GALLERY_IMAGES)
            }
        }
    }

    private fun onPermissionError(throwable: Throwable) {
        updateState { copy(isLoading = false) }
        onError(throwable)
    }

    private fun saveImageToGallery(byteArray: ByteArray){
        updateState { copy(isLoading = true) }
        tryToExecute(
            function = { imagesRepository.saveImageToGallery(byteArray) },
            onSuccess = { onDownloadSuccess() },
            onError = ::onError,
            dispatcher = dispatcher
        )
    }

    override fun onClickCopyToClipboard(clipboard: Clipboard) {
        updateState { copy(isLoading = true) }
        tryToExecute(
            function = { clipboard.setClipEntry(clipEntryOf(state.value.shareLinkUrl)) },
            onSuccess = { onCopyToClipboardSuccess() },
            onError = ::onError
        )
    }

    private fun onDownloadSuccess() {
        viewModelScope.launch(dispatcher) {
            updateState { copy(isLoading = false) }
            sendNewEffect(ShareQrCodeUIEffect.ShowClickDownloadSnackBar)
        }
    }

    private fun onCopyToClipboardSuccess() {
        viewModelScope.launch(dispatcher) {
            updateState { copy(isLoading = false) }
            sendNewEffect(ShareQrCodeUIEffect.ShowCopyToClipBoardSnackBar)
        }
    }

    private fun onError(throwable: Throwable) {
        viewModelScope.launch(dispatcher) {
            sendNewEffect(
                ShareQrCodeUIEffect.ShowSnackBarError(
                    errorStringResource = Res.string.cant_save_qr_code
                )
            )
        }
    }

    suspend fun sendNewEffect(newEffect: ShareQrCodeUIEffect) {
        _effect.emit(newEffect)
    }

    private fun updateState(updater: ShareQrCodeUIState.() -> ShareQrCodeUIState) {
        _state.update(updater)
    }

    @OptIn(ExperimentalTime::class)
    private fun <T> Flow<T>.throttleFirst(periodMillis: Long): Flow<T> {
        require(periodMillis > 0)
        return flow {
            var lastTime = 0L
            collect { value ->
                val currentTime = Clock.System.now().toEpochMilliseconds()
                if (currentTime - lastTime >= periodMillis) {
                    lastTime = currentTime
                    emit(value)
                }
            }
        }
    }

    private fun <T> tryToExecute(
        function: suspend () -> T,
        onSuccess: suspend (T) -> Unit = {},
        onError: (Throwable) -> Unit = {},
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            onError(throwable)
        }

        return viewModelScope.launch(exceptionHandler + dispatcher) {
            val result = function()
            onSuccess(result)
        }
    }

    companion object {
        const val SHARE_URL = "https://mena-dev.the-chance.net/download.html?userId="
    }
}
package net.thechance.mena.identity.presentation.screen.profile.components.dialog

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.Clipboard
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
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
import net.thechance.mena.identity.domain.repository.ImagesRepository
import net.thechance.mena.identity.presentation.screen.profile.components.dialog.share.clipEntryOf
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ShareQrCodeViewModel(
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

    private fun setUrlLinks() {
        updateState {
            copy(
                shareLinkUrl = generateLink()
            )
        }
    }

    private fun generateLink(): String {
        return "https://mena-dev.the-chance.net?userId=7f30b317-3c11-4afc-a4ed-ea84f29b0f65"
    }


    private fun onCopyToClipboardSuccess() {
        updateState { copy(showCopiedMessage = true) }
    }

    override fun onClickDownload(bitmap: ImageBitmap) {
        val permissionState = galleryPermissionHandler.checkPermission()

        when (permissionState) {
            PermissionState.GRANTED -> {
                tryToExecute(
                    function = { saveImageToGallery(bitmap) },
                    onSuccess = ::onSuccess,
                    onError = ::onError,
                    dispatcher = dispatcher
                )
            }

            PermissionState.DENIED_PERMANENTLY -> galleryPermissionHandler.openSettingPage()
            else -> galleryPermissionHandler.requestPermission()
        }
    }

    override fun onClickCopyToClipboard(clipboard: Clipboard) {
        tryToExecute(
            function = { clipboard.setClipEntry(clipEntryOf(state.value.shareLinkUrl)) },
            onSuccess = { onCopyToClipboardSuccess() },
            onError = { onDismissShareDialog() }
        )
    }

    override fun onDismissShareDialog() {
        updateState { copy() }
    }

    override fun onDismissCopyLinkSnackBar() {
        updateState { copy(showCopiedMessage = false) }
    }


    private suspend fun saveImageToGallery(bitmap: ImageBitmap) {
        val imageByteArray = bitmap.encodeToByteArray()
        imagesRepository.saveImageToGallery(imageByteArray)
    }

    private fun onSuccess(response: Unit) {
        tryToExecute(
            function = {
                sendNewEffect(ShareQrCodeUIEffect.OnClickDownload)
            }
        )
    }

    private fun onError(throwable: Throwable) {
        _state.update { it.copy(errorMessage = Res.string.cant_save_qr_code) }
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
}
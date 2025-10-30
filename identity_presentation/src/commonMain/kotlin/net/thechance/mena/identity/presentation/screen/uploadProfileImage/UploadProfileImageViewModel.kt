package net.thechance.mena.identity.presentation.screen.uploadProfileImage

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.error.ErrorState

class UploadProfileImageViewModel(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
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

    override fun onClickEdit(imageBitmap: ImageBitmap) {
        updateState { copy(imageBitmap = imageBitmap) }
    }

    private fun onUploadSuccess(result: Boolean) {
        updateState { copy(isLoading = false) }
        sendNewEffect(UploadProfileImageUIEffect.NavigateToNextScreen)
    }

    private fun onUploadError(errorState: ErrorState) {
        updateState { copy(isLoading = false) }
    }
}
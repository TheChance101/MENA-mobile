package net.thechance.mena.faith.presentation.feature.mosque.uploadImageScreen

import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.faith.presentation.base.BaseViewModel

class UploadImageViewModel(

) : BaseViewModel<UploadImageUiState, UploadImageEffect>(
    initialState = UploadImageUiState()
),
    UploadImageInteractionListener {
    override fun onImageCrop(image: ImageBitmap) {
        sendEffect(UploadImageEffect.NavigateBack(image))
    }
}
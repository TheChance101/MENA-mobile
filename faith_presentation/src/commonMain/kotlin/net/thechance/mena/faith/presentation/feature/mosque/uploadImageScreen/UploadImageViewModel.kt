package net.thechance.mena.faith.presentation.feature.mosque.uploadImageScreen

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.feature.mosque.shared.SharedImageViewModel

internal class UploadImageViewModel(
    val sharedImageViewModel: SharedImageViewModel
) : BaseViewModel<UploadImageUiState, UploadImageEffect>(
    initialState = UploadImageUiState()
), UploadImageInteractionListener {

    init {
        observeImageSrc()
        observeCroppedImage()
    }

    private fun observeImageSrc() {
        viewModelScope.launch {
            sharedImageViewModel.imageSrc.collect { imageSrc ->
                updateState { it.copy(imageSrc = imageSrc) }
            }
        }
    }

    private fun observeCroppedImage() {
        viewModelScope.launch {
            sharedImageViewModel.croppedImage.collect { croppedImage ->
                updateState { it.copy(croppedImage = croppedImage) }
            }
        }
    }

    override fun onImageCrop(image: ImageBitmap) {
        sharedImageViewModel.updateCroppedImage(image)
        updateState { it.copy(croppedImage = image) }
        sendEffect(UploadImageEffect.NavigateBack)
    }

    override fun onBackClick() {
        sendEffect(UploadImageEffect.NavigateBack)
    }
}
package net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.crop.CropError
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.CropState
import com.attafitamim.krop.core.crop.crop
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop.MosqueImageCropUiState.Companion.MAX_ZOOM
import net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop.MosqueImageCropUiState.Companion.MIN_ZOOM

internal class MosqueImageCropViewModel() :
    BaseViewModel<MosqueImageCropUiState, MosqueImageCropEffect>(
        initialState = MosqueImageCropUiState()
    ), MosqueImageCropInteractionListener {

    fun onSelectImage(imageSrc: ImageSrc?) {
        tryToExecute(
            execute = {
                uiState.value.cropper.let { cropper ->
                    when (val result = cropper.crop(imageSrc)) {
                        CropError.LoadingError -> {}
                        CropResult.Cancelled -> {}
                        is CropResult.Success -> onCropImageSuccess(result.bitmap)
                        CropError.SavingError -> {}
                    }
                }
            }
        )
    }

    override fun onSaveClicked() {
        uiState.value.cropper.cropState?.done(true)
    }

    override fun onUploadAnotherImageClicked(imageSrc: ImageSrc?) {
        onSelectImage(imageSrc)
    }

    override fun onZoomInClicked() {
        zoom(uiState.value.cropper.cropState, ZOOM_IN)
    }

    override fun onZoomOutClicked() {
        zoom(uiState.value.cropper.cropState, ZOOM_OUT)
    }

    override fun onResetClicked() {
        uiState.value.cropper.cropState?.reset()
    }

    private fun onCropImageSuccess(selectedImage: ImageBitmap) {
        sendEffect(MosqueImageCropEffect.NavigateBack(selectedImage))
    }

    private fun zoom(cropState: CropState?, factor: Float) {
        cropState?.let { cropState ->
            val region = cropState.region
            val current = cropState.transform.scale
            val newScale = Offset(
                x = (current.x * factor).coerceIn(MIN_ZOOM, MAX_ZOOM),
                y = (current.y * factor).coerceIn(MIN_ZOOM, MAX_ZOOM)
            )

            cropState.transform = cropState.transform.copy(scale = newScale)
            cropState.region = region
        }
    }

    companion object {
        private const val ZOOM_IN = 1.2f
        private const val ZOOM_OUT = 0.8f
    }
}
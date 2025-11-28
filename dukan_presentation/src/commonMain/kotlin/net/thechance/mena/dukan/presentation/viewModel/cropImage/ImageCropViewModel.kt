package net.thechance.mena.dukan.presentation.viewModel.cropImage

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.crop.CropError
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.CropState
import com.attafitamim.krop.core.crop.crop
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.cropImage.ImageCropUiState.Companion.MAX_ZOOM
import net.thechance.mena.dukan.presentation.viewModel.cropImage.ImageCropUiState.Companion.MIN_ZOOM

class ImageCropViewModel() : BaseViewModel<
        ImageCropUiState, ImageCropEffects>(
    initialState = ImageCropUiState()
), ImageCropInteractionListener {


    fun onSelectImage(imageSrc: ImageSrc?) {
        tryToExecute(
            block = {
                state.value.cropper.let { cropper ->
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
        state.value.cropper.cropState?.done(true)
    }

    override fun onUploadAnotherImageClicked(
        imageSrc: ImageSrc?
    ) {
        state.value.cropper.cropState?.reset()
        onSelectImage(imageSrc)
    }

    override fun onZoomInClicked() {
        zoom(state.value.cropper.cropState, ZOOM_IN)
    }

    override fun onZoomOutClicked() {
        zoom(state.value.cropper.cropState, ZOOM_OUT)
    }

    override fun onResetClicked() {
        state.value.cropper.cropState?.reset()
    }

    private fun onCropImageSuccess(selectedImage: ImageBitmap) {
        emitEffect(ImageCropEffects.NavigateBack(selectedImage))
    }


    private fun zoom(cropState: CropState?, factor: Float) {
        cropState?.let { cropState ->
            val region = cropState.region
            val current = cropState.transform.scale
            val newScale = Offset(
                (current.x * factor).coerceIn(MIN_ZOOM, MAX_ZOOM),
                (current.y * factor).coerceIn(MIN_ZOOM, MAX_ZOOM)
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

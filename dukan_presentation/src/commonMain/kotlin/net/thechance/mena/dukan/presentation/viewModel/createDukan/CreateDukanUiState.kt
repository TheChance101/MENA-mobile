package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.images.ImageSrc

data class CreateDukanUiState(
    val name: String = "",
    val currentStep: CreateDukanStep = CreateDukanStep.BASIC_INFORMATION,
    val isButtonEnabled: Boolean = true, // TODO: Change this to be default be false
    val isButtonLoading: Boolean = false,
    val currentLocation: CoordinatesUi = CoordinatesUi(),
    val isMapLocked: Boolean = false,
    val address: String = "",
    val savedImageUri: String? = null,
    val isNextButtonEnabled: Boolean = false,
    val zoomFactor: Float = 1f,
    val isZoomOutEnabled: Boolean = false,
    val croppedImage: ImageBitmap? = null,
    val isEditIconVisible: Boolean = false,
    val selectedImage: ImageSrc? = null,
    val isImageBeingCropped: Boolean = false,
) {

    data class CoordinatesUi(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
    )

    enum class CreateDukanStep {
        BASIC_INFORMATION,
        SELECT_IMAGE,
        SELECT_LOCATION,
        SELECT_STYLE;

        companion object {
            val steps = entries
        }
    }
}
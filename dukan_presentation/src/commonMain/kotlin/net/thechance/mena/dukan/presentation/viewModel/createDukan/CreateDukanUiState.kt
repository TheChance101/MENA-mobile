package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.presentation.screen.CreateDukan.content.component.DukanStyle

data class CreateDukanUiState(
    val name: String = "",
    val currentStep: CreateDukanStep = CreateDukanStep.BASIC_INFORMATION,
    val isButtonEnabled: Boolean = true, // TODO: Change this to be default be false
    val isButtonLoading: Boolean = false,
    val savedImageUri: String? = null,
    val isNextButtonEnabled: Boolean = false,
    val zoomFactor: Float = 1f,
    val isZoomOutEnabled: Boolean = false,
    val isEditIconVisible: Boolean = false,
    val isImageBeingCropped: Boolean = false,
) {
    enum class CreateDukanStep {
        BASIC_INFORMATION,
        SELECT_IMAGE,
        CROP_IMAGE,
        SELECT_LOCATION,
        SELECT_STYLE;
    val isButtonLoading: Boolean = false,
    val dukanColors: List<Long> = listOf(
        // TODO: Replace with colors fetched from backend
        0xFFE91E63,
        0xFF1146F3,
        0xFF4CAF50,
        0xFFE91E63,
        0xFF2196F3,
        0xFF4CAF80,
        0xFFE91E63,
        0xFF7196F9,
    ),
    val dukanStyles: List<StyleUiState> = listOf(  // TODO: Replace with styles fetched from backend
        Dukan.Style.WIDE_IMAGE,
        Dukan.Style.SMALL_IMAGE,
        Dukan.Style.NO_IMAGE
    ).map { it.toUiState() },
    val selectedColor: Long? = null,
    val selectedStyle: Dukan.Style? = null,
    val errorMessage: String? = null
)

data class StyleUiState(
    val style: Dukan.Style,
    val orientation: DukanStyle,
    val hasImage: Boolean,
    val label: String
)

        companion object {
            val steps = entries
        }
    }
}
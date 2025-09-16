package net.thechance.mena.dukan.presentation.viewModel.createDukan

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
    val dukanColors: List<Long> = listOf( // TODO: Replace with colors fetched from backend
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
        StyleUiState(
            orientation = DukanStyle.List,
            hasImage = true,
            label = "Wide image with list products"
        ),
        StyleUiState(
            orientation = DukanStyle.Grid,
            hasImage = true,
            label = "Small image with grid products"
        ),
        StyleUiState(
            orientation = DukanStyle.List,
            hasImage = false,
            label = "No dukan image"
        )
    ),
    val selectedColor: Long? = null,
    val selectedStyle: StyleUiState? = null,
    val errorMessage: String? = null
) {
    enum class CreateDukanStep {
        BASIC_INFORMATION,
        SELECT_IMAGE,
        CROP_IMAGE,
        SELECT_LOCATION,
        SELECT_STYLE;

        companion object {
            val steps = entries
        }
    }
}

data class StyleUiState(
    val orientation: DukanStyle,
    val hasImage: Boolean,
    val label: String
)

enum class DukanStyle {
    Grid,
    List
}
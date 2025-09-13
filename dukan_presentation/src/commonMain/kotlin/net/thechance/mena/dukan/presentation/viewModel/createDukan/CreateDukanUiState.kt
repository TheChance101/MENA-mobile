package net.thechance.mena.dukan.presentation.viewModel.createDukan

data class CreateDukanUiState(
    val name: String = "",
    val currentStep: Int = 0,
    val isButtonEnabled: Boolean = true,
    val isButtonLoading: Boolean = false,
    val savedImageUri: String? = null,
    val isNextButtonEnabled: Boolean = false,
    val zoomFactor: Float = 1f,
    val isZoomOutEnabled: Boolean = false
)
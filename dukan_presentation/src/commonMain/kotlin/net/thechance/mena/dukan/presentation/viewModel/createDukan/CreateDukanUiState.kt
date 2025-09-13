package net.thechance.mena.dukan.presentation.viewModel.createDukan

data class CreateDukanUiState(
    val name: String = "",
    val currentStep: Int = 0,
    val isButtonEnabled: Boolean = true, // TODO: Change this to be default be false
    val isButtonLoading: Boolean = false
)

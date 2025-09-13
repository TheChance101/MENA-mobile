package net.thechance.mena.dukan.presentation.viewModel.createDukan

import net.thechance.mena.dukan.domain.entity.Dukan

data class CreateDukanUiState(
    val name: String = "",
    val currentStep: Int = 0,
    val isButtonEnabled: Boolean = true, // TODO: Change this to be default be false
    val isButtonLoading: Boolean = false,
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
    val dukanStyles: List<Dukan.Style> = listOf(  // TODO: Replace with styles fetched from backend
        Dukan.Style.WIDE_IMAGE,
        Dukan.Style.SMALL_IMAGE,
        Dukan.Style.NO_IMAGE
    ),
    val selectedColor: Long? = null,
    val selectedStyle: Dukan.Style? = null,
    val errorMessage: String? = null
)

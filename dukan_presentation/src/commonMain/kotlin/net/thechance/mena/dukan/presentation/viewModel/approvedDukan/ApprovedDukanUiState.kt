package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.presentation.viewModel.createDukan.DukanCategoryUiState

data class ApprovedDukanUiState(
    val shelves: List<Shelf> = emptyList(),
    val categories: List<DukanCategoryUiState> = listOf(
        DukanCategoryUiState(id = "clothes", name = "Clothes", imageUrl = ""),
        DukanCategoryUiState(id = "shoes", name = "Shoes", imageUrl = "")
    ),
    val selectedCategories: Set<DukanCategoryUiState> = emptySet(),
    val productCount: Int = 0,
    val isLoading: Boolean = false,
    val showSnackBar: Boolean = false
)

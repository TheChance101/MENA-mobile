package net.thechance.mena.dukan.presentation.viewModel.dukanCategories

import net.thechance.mena.dukan.presentation.component.SnackBarUiState

data class DukanCategoriesUiState(
    val snackBarUiState: SnackBarUiState?=null,
    val categories: List<CategoryUiState> = emptyList(),
)

data class CategoryUiState(
    val id: String,
    val name: String,
    val imageUrl: String,
)
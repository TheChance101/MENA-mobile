package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.presentation.component.SnackBarUiState

data class ApprovedDukanUiState(
    val shelves: List<Shelf> = emptyList(),
    val availableShelves: List<Shelf> = emptyList(),
    val selectedShelves: Set<Shelf> = emptySet(),
    val products: List<Product> = emptyList(),
    val totalProducts: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingProducts: Boolean = false,
    val snackBarState: SnackBarUiState? = null
)

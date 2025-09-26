package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.presentation.component.SelectableItem

data class ApprovedDukanUiState(
    val shelves: List<Shelf> = emptyList(),
    val availableShelves: List<ShelfUiState> = emptyList(),
    val selectedShelves: Set<ShelfUiState> = emptySet(),
    val productCount: Int = 0,
    val isLoading: Boolean = false,
    val showSnackBar: Boolean = false
)

data class ShelfUiState(
    override val id: String,
    override val name: String,
    override val imageUrl: String = ""
) : SelectableItem

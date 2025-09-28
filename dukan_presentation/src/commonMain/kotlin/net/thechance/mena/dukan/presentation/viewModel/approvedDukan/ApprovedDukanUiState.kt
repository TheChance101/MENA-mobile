package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.delete
import mena.dukan_presentation.generated.resources.delete_shelf_description
import mena.dukan_presentation.generated.resources.delete_shelf_title
import mena.dukan_presentation.generated.resources.dismiss
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import org.jetbrains.compose.resources.StringResource

data class ApprovedDukanUiState(
    val shelves: List<Shelf> = emptyList(),
    val availableShelves: List<Shelf> = emptyList(),
    val selectedShelves: Set<Shelf> = emptySet(),
    val products: List<Product> = emptyList(),
    val productCount: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingProducts: Boolean = false,
    val showSnackBar: Boolean = false,
    val snackBarState: SnackBarUiState= SnackBarUiState(),
    val deleteShelfConfirmationDialogUiState: DeleteShelfConfirmationDialogUiState = DeleteShelfConfirmationDialogUiState(),
    val showDeleteConfirmationDialog: Boolean = false,
)
data class DeleteShelfConfirmationDialogUiState(
    val title: StringResource = Res.string.delete_shelf_title,
    val description: StringResource = Res.string.delete_shelf_description,
    val type: ConfirmDialogType = ConfirmDialogType.DELETE
)

enum class ConfirmDialogType(val text: StringResource) {
    DELETE(text = Res.string.delete),
    DISMISS(text = Res.string.dismiss)
}
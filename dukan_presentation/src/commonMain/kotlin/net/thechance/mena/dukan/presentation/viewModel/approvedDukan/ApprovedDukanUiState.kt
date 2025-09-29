package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.delete
import mena.dukan_presentation.generated.resources.dismiss
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import org.jetbrains.compose.resources.StringResource

data class ApprovedDukanUiState(
    val shelves: List<Shelf> = emptyList(),
    val availableShelves: List<Shelf> = emptyList(),
    val selectedShelf: Shelf? = null,
    val products: List<Product> = emptyList(),
    val totalProducts: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingProducts: Boolean = false,
    val snackBarState: SnackBarUiState? = null,
    val deleteShelfConfirmationDialogUiState: DeleteShelfConfirmationDialogUiState? = null,
    val showDeleteConfirmationDialog: Boolean = false,
)

data class DeleteShelfConfirmationDialogUiState(
    val title: StringResource,
    val description: StringResource,
    val type: ConfirmDialogType
)

enum class ConfirmDialogType(val text: StringResource) {
    DELETE(text = Res.string.delete),
    DISMISS(text = Res.string.dismiss)
}
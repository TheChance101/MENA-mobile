package net.thechance.mena.dukan.presentation.viewModel.manageDukan

import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.delete
import mena.dukan_presentation.generated.resources.dismiss
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import org.jetbrains.compose.resources.StringResource

data class ManageDukanUiState(
    val shelves: List<ShelfUiState> = emptyList(),
    val selectedShelf: ShelfUiState? = null,
    val totalProducts: Long = 0,
    val products: PagingData<ProductUiState> = PagingData(),
    val isLoading: Boolean = false,
    val isLoadingProducts: Boolean = false,
    val snackBarState: SnackBarUiState? = null,
    val deleteShelfConfirmationDialogUiState: DeleteShelfConfirmationDialogUiState? = null,
    val showDeleteConfirmationDialog: Boolean = false,
)

data class DeleteShelfConfirmationDialogUiState(
    val title: StringResource,
    val description: StringResource,
    val type: ConfirmDialogType,
    val shelfId: String
)

enum class ConfirmDialogType(val text: StringResource) {
    DELETE(text = Res.string.delete),
    DISMISS(text = Res.string.dismiss)
}

data class ShelfUiState(
    val id: String,
    val name: String
)


class ProductUiState(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val price: Double = 0.0,
    val imageUrl: String = ""
)
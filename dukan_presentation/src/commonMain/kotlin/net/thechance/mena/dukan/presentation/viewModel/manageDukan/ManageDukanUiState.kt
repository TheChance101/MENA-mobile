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
    val shelvesState: ShelvesState = ShelvesState.LOADING,
    val productState: ProductsState = ProductsState.LOADING,
    val snackBarState: SnackBarUiState? = null,
    val deleteShelfConfirmationDialogUiState: DeleteShelfConfirmationDialogUiState? = null,
    val showDeleteConfirmationDialog: Boolean = false,
)

data class DeleteShelfConfirmationDialogUiState(
    val title: StringResource,
    val description: StringResource,
    val type: ConfirmDialogType,
    val shelfId: String,
    val isDialogVisible: Boolean = false
)

enum class ConfirmDialogType(val text: StringResource) {
    DELETE(text = Res.string.delete),
    DISMISS(text = Res.string.dismiss)
}

enum class ShelvesState {
    LOADING,
    LOADED,
    EMPTY
}

enum class ProductsState {
    LOADING,
    LOADED,
    EMPTY
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
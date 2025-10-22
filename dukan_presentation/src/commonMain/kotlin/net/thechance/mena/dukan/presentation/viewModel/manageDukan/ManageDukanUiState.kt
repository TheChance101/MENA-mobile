package net.thechance.mena.dukan.presentation.viewModel.manageDukan

import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.delete
import mena.dukan_presentation.generated.resources.dismiss
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import org.jetbrains.compose.resources.StringResource

data class ManageDukanUiState(
    val shelves: List<ManageDukanUiState.ShelfUiState> = emptyList(),
    val selectedShelf: ManageDukanUiState.ShelfUiState? = null,
    val totalProducts: Long = 0,
    val products: PagingData<ManageDukanUiState.ProductUiState> = PagingData(),
    val shelvesState: ShelvesState = ShelvesState.LOADING,
    val productState: ProductsState = ProductsState.LOADING,
    val snackBarState: SnackBarUiState? = null,
    val deleteDialog: DeleteDialogState? = null
) {
    data class DeleteDialogState(
        val title: StringResource,
        val description: StringResource,
        val type: DialogType,
        val shelfId: String,
    )

    data class ShelfUiState(
        val id: String,
        val name: String
    )

    data class ProductUiState(
        val id: String = "",
        val name: String = "",
        val description: String? = null,
        val price: Double = 0.0,
        val imageUrl: String = ""
    )

    enum class DialogType(val text: StringResource) {
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
}
@file:OptIn(ExperimentalUuidApi::class)
package net.thechance.mena.dukan.presentation.viewModel.manageDukan

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.delete
import mena.dukan_presentation.generated.resources.dismiss
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi

data class ManageDukanUiState(
    val shelves: List<ShelfUiState> = emptyList(),
    val selectedShelf: ShelfUiState? = null,
    val totalProducts: Long = 0,
    val products: Flow<PagingData<ProductUiState>> = emptyFlow(),
    val shelvesState: ShelvesState = ShelvesState.LOADING,
    val snackBarState: SnackBarUiState? = null,
    val deleteDialog: DeleteDialogState? = null,
    val activationStatus: ActivationStatus = ActivationStatus.ACTIVATED,
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
        val basePrice: Double = 0.0,
        val finalPrice: Double = 0.0,
        val imageUrl: String = "",
        val isOutOfStock: Boolean = false
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

    enum class ActivationStatus {
        ACTIVATED,
        DEACTIVATED,
        ONHOLD
    }
}
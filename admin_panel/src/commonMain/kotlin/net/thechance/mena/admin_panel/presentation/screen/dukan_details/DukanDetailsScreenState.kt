@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.admin_panel.presentation.screen.dukan_details

import net.thechance.mena.admin_panel.domain.entity.dukan.Product
import net.thechance.mena.admin_panel.domain.entity.dukan.Shelf
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.activate_dukan
import net.thechance.mena.admin_panel.resources.deactivate
import net.thechance.mena.admin_panel.resources.ic_activate
import net.thechance.mena.admin_panel.resources.ic_block
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class DukanDetailsScreenState(
    val isDukanDetailsLoading: Boolean = false,
    val isShelvesLoading: Boolean = false,
    val isProductsLoading: Boolean = false,
    val errorState: ErrorState? = null,
    val snackBar: SnackBarState = SnackBarState(),
    val dukan: DukanItemUiState = DukanItemUiState(),
    val shelves: List<Shelf> = listOf(),
    val selectedShelfId: String = "",
    val totalShelves: String = "",
    val products: List<Product> = listOf(),
    val isDeactivateDukanDialogShown: Boolean = false,
    val isMapVisible: Boolean = true,
    val deactivateReason: String = "",
    val isDeactivateBtnLoading: Boolean = false,
    val isActiveDukanLoading: Boolean = false,
) {
    val isDeactivateBtnEnabled: Boolean
        get() = deactivateReason.length > 1

    data class DukanItemUiState(
        val id: Uuid = Uuid.random(),
        val name: String = "",
        val address: String = "",
        val imageUrl: String = "",
        val categories: List<String> = listOf(),
        val dukanStatus: DukanStatus = DukanStatus.DEACTIVE,
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    )

    enum class DukanStatus(
        val text: StringResource,
        val icon: DrawableResource
    ) {
        DEACTIVE(text = Res.string.activate_dukan, icon = Res.drawable.ic_activate),
        ACTIVE(text = Res.string.deactivate, icon = Res.drawable.ic_block)
    }
}
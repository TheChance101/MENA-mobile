@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.mainScreen

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.DukanCategoryUiState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class MainScreenUiState(
    val snackBarState: SnackBarUiState? = null,
    val dukanState: DukanState = DukanState(),
    val isConnected: Boolean = true,
    val categories: List<DukanCategoryUiState> = emptyList(),
    val bestNearestDukans: Flow<PagingData<BestNearestDukanUiState>> = emptyFlow(),
    val editorPickDukans: Flow<PagingData<EditorPickDukanUiState>> = emptyFlow(),
    val dukanTopDiscount: List<DukanTopDiscount> = emptyList(),
    val isContentLoading: Boolean = false,
) {
    data class DukanState(
        val name: String = "",
        val status: DukanStatusUi = DukanStatusUi.Loading
    )

    data class BestNearestDukanUiState(
        val id: String,
        val name: String,
        val imageUrl: String
    )

    data class EditorPickDukanUiState(
        val id: String,
        val name: String,
        val imageUrl: String,
        val isFavorite: Boolean
    )

    data class DukanTopDiscount(
        val id: Uuid,
        val imageUrl: String,
        val discount : Int
    )
    enum class DukanStatusUi {
        Loading,
        Pending,
        None,
        Approved,
        Default
    }
}
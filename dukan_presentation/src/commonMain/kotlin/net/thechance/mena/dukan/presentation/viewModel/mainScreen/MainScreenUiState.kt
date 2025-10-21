package net.thechance.mena.dukan.presentation.viewModel.mainScreen

import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.DukanCategoryUiState

data class MainScreenUiState(
    val snackBarState: SnackBarUiState? = null,
    val dukanState: DukanState = DukanState(),
    val isConnected: Boolean = true,
    val categories: List<DukanCategoryUiState> = emptyList(),
    val bestNearestDukans: PagingData<BestNearestDukanUiState> = PagingData(),
    val bestNearestDukanState: BestNearestDukanStatus = BestNearestDukanStatus.LOADING,
    val editorPickDukans: PagingData<EditorPickDukanUiState> = PagingData(),
    val editorPickDukanState: EditorPickDukanStatus = EditorPickDukanStatus.LOADING,
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
    )
    enum class DukanStatusUi {
        Loading,
        Pending,
        None,
        Approved
    }
    enum class BestNearestDukanStatus {
        LOADING,
        LOADED,
    }

    enum class EditorPickDukanStatus {
        LOADING,
        LOADED
    }
}
package net.thechance.mena.dukan.presentation.viewModel.mainScreen

import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.viewModel.createDukan.DukanCategoryUiState

data class MainScreenUiState(
    val errorMessage: String? = null,
    val dukanState: DukanState = DukanState(),
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
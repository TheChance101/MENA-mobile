package net.thechance.mena.dukan.presentation.viewModel.mainScreen

import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.viewModel.createDukan.DukanCategoryUiState

data class MainScreenUiState(
    val errorMessage: String? = null,
    val dukanState: DukanState = DukanState(),
    val categories: List<DukanCategoryUiState> = emptyList(),
    val bestNearestDukans: PagingData<BestNearestDukanUiState> = PagingData(),
    val editorPickDukans: PagingData<EditorPickDukanUiState> = PagingData(),
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
}
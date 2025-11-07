package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState

fun fakeDukans(): List<MainScreenUiState.EditorPickDukanUiState> {
    return listOf(
        MainScreenUiState.EditorPickDukanUiState(
            id = "1",
            name = "Dukan Market",
            isFavorite = true,
            imageUrl = "https://picsum.photos/200/200?1"
        ),
        MainScreenUiState.EditorPickDukanUiState(
            id = "2",
            name = "Fresh & Best",
            isFavorite = true,
            imageUrl = "https://picsum.photos/200/200?2"
        ),
        MainScreenUiState.EditorPickDukanUiState(
            id = "3",
            name = "City Dukan",
            isFavorite = false,
            imageUrl = "https://picsum.photos/200/200?3"
        ),
        MainScreenUiState.EditorPickDukanUiState(
            id = "4",
            name = "Happy Store",
            isFavorite = false,
            imageUrl = "https://picsum.photos/200/200?4"
        )
    )
}
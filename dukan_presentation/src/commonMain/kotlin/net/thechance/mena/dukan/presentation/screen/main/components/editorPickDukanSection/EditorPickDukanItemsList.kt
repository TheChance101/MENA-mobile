package net.thechance.mena.dukan.presentation.screen.main.components.editorPickDukanSection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditorPickDukanItemsList(
    dukans: List<MainScreenUiState.EditorPickDukanUiState> = emptyList(),
    onDukanClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        modifier = modifier
    ) {
        items(dukans) { dukan ->
            EditorPickDukanItem(
                dukanName = dukan.name,
                dukanImage = dukan.imageUrl,
                onClick = { onDukanClick(dukan.id) }
            )
        }
    }
}

@Preview
@Composable
private fun EditorPickDukanItemsListPreview() {
    MenaTheme {
        EditorPickDukanItemsList(
            fakeDukans(),
            onDukanClick = {}
        )
    }
}

fun fakeDukans(): List<MainScreenUiState.EditorPickDukanUiState> {
    return listOf(
        MainScreenUiState.EditorPickDukanUiState(
            id = "1",
            name = "Dukan Market",
            imageUrl = "https://picsum.photos/200/200?1"
        ),
        MainScreenUiState.EditorPickDukanUiState(
            id = "2",
            name = "Fresh & Best",
            imageUrl = "https://picsum.photos/200/200?2"
        ),
        MainScreenUiState.EditorPickDukanUiState(
            id = "3",
            name = "City Dukan",
            imageUrl = "https://picsum.photos/200/200?3"
        ),
        MainScreenUiState.EditorPickDukanUiState(
            id = "4",
            name = "Happy Store",
            imageUrl = "https://picsum.photos/200/200?4"
        )
    )
}
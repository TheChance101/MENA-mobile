package net.thechance.mena.dukan.presentation.screen.main.components.bestNersetDukanSection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun BestNearestDukanSection(
    dukans: List<MainScreenUiState.BestNearestDukanUiState>,
    onDukanClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(dukans) { dukan ->
            NearDukanItem(
                dukanName = dukan.name,
                dukanImage = dukan.imageUrl,
                onClick = { onDukanClick(dukan.id) }
            )
        }
    }
}

@Preview
@Composable
private fun BestNearestDukanSectionPreview() {
    MenaTheme {
        BestNearestDukanSection(dukans = fakeBestNearestDuknas(), onDukanClick = {})
    }
}

fun fakeBestNearestDuknas(): List<MainScreenUiState.BestNearestDukanUiState> {
    return listOf(
        MainScreenUiState.BestNearestDukanUiState(
            id = "1",
            name = "Dukan Market",
            imageUrl = "https://picsum.photos/200/200?1"
        ),
        MainScreenUiState.BestNearestDukanUiState(
            id = "2",
            name = "Fresh & Best",
            imageUrl = "https://picsum.photos/200/200?2"
        ),
        MainScreenUiState.BestNearestDukanUiState(
            id = "3",
            name = "City Dukan",
            imageUrl = "https://picsum.photos/200/200?3"
        ),
        MainScreenUiState.BestNearestDukanUiState(
            id = "4",
            name = "Happy Store",
            imageUrl = "https://picsum.photos/200/200?4"
        )
    )
}
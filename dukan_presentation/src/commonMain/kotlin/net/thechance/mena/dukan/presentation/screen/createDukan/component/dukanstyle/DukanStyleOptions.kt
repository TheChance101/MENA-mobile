package net.thechance.mena.dukan.presentation.screen.createDukan.component.dukanstyle

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewCreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DukanStyleOptions(
    listener: CreateDukanInteractionListener,
    state: CreateDukanUiState
) {
    Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)) {
        state.dukanStyles.forEach { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._4),
                modifier = Modifier.weight(1f)
            ) {
                val textStyleColor by animateColorAsState(
                    if (state.selectedStyle == item.style) Theme.colorScheme.shadePrimary else Theme.colorScheme.shadeSecondary
                )

                when (item.style) {
                    CreateDukanUiState.Style.WIDE_IMAGE -> WideImageStyle(
                        state = state,
                        isSelected = state.selectedStyle == item.style,
                        onClick = { listener.onStyleClicked(item.style) }
                    )

                    CreateDukanUiState.Style.SMALL_IMAGE -> SmallImageStyle(
                        state = state,
                        isSelected = state.selectedStyle == item.style,
                        onClick = { listener.onStyleClicked(item.style) }
                    )

                    CreateDukanUiState.Style.NO_IMAGE -> NoImageStyle(
                        state = state,
                        isSelected = state.selectedStyle == item.style,
                        onClick = { listener.onStyleClicked(item.style) }
                    )
                }
                Text(
                    text = item.name,
                    style = Theme.typography.label.small,
                    color = textStyleColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDukanStyleOptions() {
    val mockState = CreateDukanUiState(
        dukanStyles = listOf(
            CreateDukanUiState.DukanStyleUiState(
                style = CreateDukanUiState.Style.WIDE_IMAGE,
                name = "Wide Image"
            ),
            CreateDukanUiState.DukanStyleUiState(
                style = CreateDukanUiState.Style.SMALL_IMAGE,
                name = "Small Image"
            ),
            CreateDukanUiState.DukanStyleUiState(
                style = CreateDukanUiState.Style.NO_IMAGE,
                name = "No Image"
            )
        ),
        selectedStyle = CreateDukanUiState.Style.SMALL_IMAGE
    )
    MenaTheme {
        DukanStyleOptions(
            listener = PreviewCreateDukanInteractionListener,
            state = mockState
        )
    }
}
package net.thechance.mena.dukan.presentation.screen.createDukan.component.dukanstyle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoImageStyle(
    state: CreateDukanUiState,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val defaultColor = Theme.colorScheme.background.surfaceHigh
    val selectedColor = state.selectedColor?.let { Color(it.color) } ?: defaultColor

    Box(
        modifier = modifier
            .height(198.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = MutableInteractionSource()
            ).then(
                if (isSelected) Modifier.border(
                    1.dp,
                    Theme.colorScheme.primary.primary,
                    RoundedCornerShape(Theme.radius.md)
                )
                else Modifier
            ),
    ) {
        Column(
            Modifier
                .padding(start = Theme.spacing._4)
                .wrapContentHeight(unbounded = true, align = Alignment.Top)
        ) {
            ShimmerTextPlaceholders(selectedColor)
            ShimmerRow(selectedColor)
            repeat(4) {
                ListItemStyle(
                    cartBackgroundColor = selectedColor,
                    modifier = Modifier
                        .padding(
                            bottom = Theme.spacing._4,
                            end = Theme.spacing._4
                        )
                )
            }
        }
    }
}

@Preview
@Composable
private fun DukanStylePreview() {
    MenaTheme {
        NoImageStyle(
            state = CreateDukanUiState(),
            isSelected = true,
            onClick = {},
        )
    }
}


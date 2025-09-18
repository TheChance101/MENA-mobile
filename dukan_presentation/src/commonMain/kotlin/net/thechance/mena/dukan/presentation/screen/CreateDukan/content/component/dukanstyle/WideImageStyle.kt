package net.thechance.mena.dukan.presentation.screen.createDukan.content.component.dukanstyle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_image
import mena.dukan_presentation.generated.resources.style_has_image
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WideImageStyle(
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
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(
                    1.dp,
                    Theme.colorScheme.primary.primary,
                    RoundedCornerShape(Theme.radius.md)
                )
                else Modifier
            ),
    ) {
        Column{
            WideImageBox(modifier = Modifier.padding(Theme.spacing._4))
            ShimmerRow(selectedColor)
            repeat(3) {
                ListItemStyle(
                    cartBackgroundColor = selectedColor,
                    modifier = Modifier
                        .padding(
                            start = Theme.spacing._4,
                            end = Theme.spacing._4,
                            bottom = Theme.spacing._4
                        )
                )
            }
        }
    }
}

@Composable
fun WideImageBox(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Theme.radius.sm))
            .background(Theme.colorScheme.background.surface)
            .padding(vertical = Theme.spacing._12),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_image),
            tint = Theme.colorScheme.primary.onPrimary,
            contentDescription = stringResource(Res.string.style_has_image),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview
@Composable
private fun DukanStylePreview() {
    MenaTheme {
        WideImageStyle(
            state = CreateDukanUiState(),
            isSelected = true,
            onClick = {},
        )
    }
}

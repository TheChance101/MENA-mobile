package net.thechance.mena.dukan.presentation.screen.createDukan.content.component.dukanstyle

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_image
import mena.dukan_presentation.generated.resources.style_has_small_image_icon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SmallImageStyle(
    state: CreateDukanUiState,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultColor = Theme.colorScheme.background.surfaceHigh
    val selectedColor = state.selectedColor?.let { Color(it.color) } ?: defaultColor

    val imageIconPadding by animateDpAsState(
        if (selectedColor == defaultColor) 6.dp else Theme.spacing._16
    )
    val verticalColumnPadding by animateDpAsState(
        if (selectedColor == defaultColor) 0.dp else Theme.spacing._4
    )
    val lastRowPaddingBottom by animateDpAsState(
        if (selectedColor == defaultColor) 10.dp else 0.dp
    )
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
        Column {
            SmallImage(
                Modifier.padding(
                    start = Theme.spacing._4,
                    bottom = imageIconPadding,
                    top = Theme.spacing._4,
                )
            )
            ShimmerRow(selectedColor)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Theme.spacing._4),
                verticalArrangement = Arrangement.spacedBy(verticalColumnPadding)
            ) {
                repeat(2) { rowIndex ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = if (rowIndex == 1) lastRowPaddingBottom else 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
                    ) {
                        repeat(2) {
                            GridItemStyle(
                                cartBackgroundColor = selectedColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmallImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.full))
            .background(Theme.colorScheme.background.surface)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_image),
            tint = Theme.colorScheme.primary.onPrimary,
            contentDescription = stringResource(Res.string.style_has_small_image_icon),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview
@Composable
private fun DukanStylePreview() {
    MenaTheme {
        SmallImageStyle(
            state = CreateDukanUiState(),
            isSelected = true,
            onClick = {},
        )
    }
}

package net.thechance.mena.dukan.presentation.screen.createDukan.component.dukanstyle

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
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
import sv.lib.squircleshape.SquircleShape

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

    Box(
        modifier = modifier
            .height(198.dp)
            .clip(SquircleShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = MutableInteractionSource()
            ).then(
                if (isSelected) Modifier.border(
                    1.dp,
                    Theme.colorScheme.primary.primary,
                    SquircleShape(Theme.radius.md)
                )
                else Modifier
            ),
    ) {
        SmallImageContent(
            selectedColor = selectedColor,
            defaultColor = defaultColor,
            imageIconPadding = imageIconPadding
        )
    }
}

@Composable
private fun SmallImageContent(
    selectedColor: Color,
    defaultColor: Color,
    imageIconPadding: Dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = Theme.spacing._4)
    ) {
        SmallImage(
            Modifier.padding(
                bottom = imageIconPadding,
                top = Theme.spacing._4,
            )
        )
        ShimmerRow(selectedColor)
        SmallImageGrid(selectedColor, defaultColor)
    }
}

@Composable
private fun SmallImageGrid(
    selectedColor: Color,
    defaultColor: Color
) {
    val verticalColumnPadding by animateDpAsState(
        if (selectedColor == defaultColor) 0.dp else Theme.spacing._4
    )
    val lastRowPaddingBottom by animateDpAsState(
        if (selectedColor == defaultColor) 10.dp else 0.dp
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = Theme.spacing._4),
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

@Composable
private fun SmallImage(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(Res.drawable.ic_image),
        tint = Theme.colorScheme.primary.onPrimary,
        contentDescription = stringResource(Res.string.style_has_small_image_icon),
        modifier = modifier
            .clip(CircleShape)
            .background(Theme.colorScheme.background.surface)
            .padding(Theme.spacing._4 + Theme.spacing._2)
            .size(20.dp)
    )
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

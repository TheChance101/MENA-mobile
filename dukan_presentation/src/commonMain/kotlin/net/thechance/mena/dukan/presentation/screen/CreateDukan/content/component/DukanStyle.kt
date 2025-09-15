package net.thechance.mena.dukan.presentation.screen.CreateDukan.content.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_image
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class DukanStyle {
    VERTICAL,
    HORIZONTAL
}

@Composable
fun DukanStyle(
    state: CreateDukanUiState,
    orientation: DukanStyle = DukanStyle.HORIZONTAL,
    hasImage: Boolean = true,
    isSelected: Boolean = true,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val defaultColor = Theme.colorScheme.background.surfaceHigh

    val baseModifier = modifier
        .height(198.dp)
        .clip(RoundedCornerShape(Theme.radius.md))
        .background(Theme.colorScheme.background.surfaceLow)
        .clickable { onClick() }
        .then(
            if (isSelected) Modifier.border(
                1.dp,
                Theme.colorScheme.primary.primary,
                RoundedCornerShape(Theme.radius.md)
            )
            else Modifier
        )

    Box(
        modifier = baseModifier,
    ) {
        when (orientation) {
            DukanStyle.HORIZONTAL -> HorizontalContent(
                selectedColor = state.selectedColor?.let { Color(it) } ?: defaultColor,
                hasImage = hasImage,
            )

            DukanStyle.VERTICAL -> VerticalContent(
                selectedColor = state.selectedColor?.let { Color(it) } ?: defaultColor,
            )
        }
    }
}

@Composable
private fun HorizontalContent(
    selectedColor: Color,
    hasImage: Boolean,
) {

    Theme.colorScheme.background.surfaceHigh

    Column(
        Modifier.wrapContentHeight(unbounded = true)
    ) {
        if (hasImage) {
            ImageBox(
                modifier = Modifier.padding(Theme.spacing._4)
            )
        } else {
            TextPlaceholders(selectedColor)
        }
        PlaceholderRow(selectedColor)

        val count = if (hasImage) 3 else 4

        repeat(count) {
            HorizontalItemStyle(
                addToCartBackgroundColor = selectedColor,
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

@Composable
private fun VerticalContent(selectedColor: Color) {
    val color = Theme.colorScheme.background.surfaceHigh

    Column {
        SmallImageIcon(
            Modifier.padding(
                start = Theme.spacing._4,
                bottom = if (selectedColor == color) 6.dp else Theme.spacing._16,
                top = Theme.spacing._4,
            )
        )
        PlaceholderRow(selectedColor)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Theme.spacing._4),
            verticalArrangement = Arrangement.spacedBy(if (selectedColor == color) 0.dp else Theme.spacing._4)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
            ) {
                VerticalItemStyle(
                    addToCartBackgroundColor = selectedColor,
                    modifier = Modifier.weight(1f)
                )
                VerticalItemStyle(
                    addToCartBackgroundColor = selectedColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.weight(1f)
                    .padding(bottom = if (selectedColor == color) 10.dp else 0.dp),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
            ) {
                VerticalItemStyle(
                    addToCartBackgroundColor = selectedColor,
                    modifier = Modifier.weight(1f)
                )
                VerticalItemStyle(
                    addToCartBackgroundColor = selectedColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PlaceholderRow(backgroundColor: Color) {
    val color = Theme.colorScheme.background.surfaceHigh

    Row(
        modifier = Modifier.padding(bottom = if (backgroundColor == color) Theme.spacing._4 else Theme.spacing._8)
    ) {
        repeat(3) {
            ShimmerRectangle(
                modifier = Modifier.weight(1f)
                    .padding(start = Theme.spacing._4, top = Theme.spacing._4),
                height = 10.dp,
                topEnd = if (it == 2) 0.dp else Theme.radius.xxs,
                bottomEnd = if (it == 2) 0.dp else Theme.radius.xxs,
                backgroundColor = backgroundColor
            )
        }
    }
}

@Composable
private fun ImageBox(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surface)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.radius.sm))
                .background(Theme.colorScheme.background.surface),
            contentAlignment = Alignment.Center
        ) {
            MenaIcon(
                painter = painterResource(Res.drawable.ic_image),
                tint = Theme.colorScheme.primary.onPrimary,
                contentDescription = "style has image",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun SmallImageIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.full))
            .background(Theme.colorScheme.background.surface)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        MenaIcon(
            painter = painterResource(Res.drawable.ic_image),
            tint = Theme.colorScheme.primary.onPrimary,
            contentDescription = "style has image",
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun TextPlaceholders(
    colorSelected: Color
) {
    val color = Theme.colorScheme.background.surfaceHigh
    ShimmerRectangle(
        modifier = Modifier.fillMaxWidth()
            .padding(
                start = Theme.spacing._4,
                top = if (colorSelected == color) 17.dp else 24.dp,
                end = 11.dp
            ),
        backgroundColor = Theme.colorScheme.background.surface,
        height = 7.dp,
    )
    ShimmerRectangle(
        modifier = Modifier.fillMaxWidth()
            .padding(
                start = Theme.spacing._4,
                end = 35.dp,
                top = Theme.spacing._2,
                bottom = if (colorSelected == color) Theme.spacing._8 else 11.dp
            ),
        backgroundColor = Theme.colorScheme.background.surface,
        height = 5.dp,
    )
}


@Composable
fun ShimmerRectangle(
    topStart: Dp = Theme.radius.xxs,
    topEnd: Dp = Theme.radius.xxs,
    bottomStart: Dp = Theme.radius.xxs,
    bottomEnd: Dp = Theme.radius.xxs,
    height: Dp,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(height).clip(
            RoundedCornerShape(
                topEnd = topEnd,
                topStart = topStart,
                bottomEnd = bottomEnd,
                bottomStart = bottomStart
            )
        )
            .background(backgroundColor)
    )
}

@Preview
@Composable
private fun DukanStylePreview() {
    MenaTheme {
        DukanStyle(
            state = CreateDukanUiState(),
            hasImage = false,
            isSelected = true
        )
    }
}

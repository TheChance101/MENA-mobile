package net.thechance.mena.dukan.presentation.screen.createDukan.component.dukanstyle

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun ShimmerTextPlaceholders(
    selectedColor: Color
) {
    val color = Theme.colorScheme.background.surfaceHigh
    val shimmerTopPadding by animateDpAsState(
        if (selectedColor == color) 12.dp else 17.dp
    )
    val shimmerBottomPadding by animateDpAsState(
        if (selectedColor == color) Theme.spacing._8 else 11.dp
    )
    ShimmerItemRectangle(
        modifier = Modifier.fillMaxWidth(0.9f)
            .padding(
                top = shimmerTopPadding,
            ),
        backgroundColor = Theme.colorScheme.background.surface,
        height = 7.dp,
    )
    ShimmerItemRectangle(
        modifier = Modifier.fillMaxWidth(0.7f)
            .padding(
                top = Theme.spacing._2,
                bottom = shimmerBottomPadding
            ),
        backgroundColor = Theme.colorScheme.background.surface,
        height = 5.dp,
    )
}

@Composable
fun ShimmerRow(selectedColor: Color) {
    val color = Theme.colorScheme.background.surfaceHigh
    val shimmerRowPadding by animateDpAsState(
        if (selectedColor == color) Theme.spacing._4 else Theme.spacing._8
    )

    Row(
        modifier = Modifier.padding(bottom = shimmerRowPadding),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
    ) {
        repeat(3) { index ->
            ShimmerItemRectangle(
                modifier = Modifier.weight(1f)
                    .padding( top = Theme.spacing._4),
                height = 10.dp,
                topEndRadius = if (index == 2) 0.dp else Theme.radius.xxs,
                bottomEndRadius = if (index == 2) 0.dp else Theme.radius.xxs,
                backgroundColor = if (index == 0) selectedColor else color
            )
        }
    }
}

@Composable
fun ShimmerItemRectangle(
    topStartRadius: Dp = Theme.radius.xxs,
    topEndRadius: Dp = Theme.radius.xxs,
    bottomStartRadius: Dp = Theme.radius.xxs,
    bottomEndRadius: Dp = Theme.radius.xxs,
    height: Dp,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(height).clip(
            RoundedCornerShape(
                topEnd = topEndRadius,
                topStart = topStartRadius,
                bottomEnd = bottomEndRadius,
                bottomStart = bottomStartRadius
            )
        ).background(backgroundColor)
    )
}

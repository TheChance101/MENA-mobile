package net.thechance.mena.dukan.presentation.screen.dukanDetails.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.unit.Dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun Modifier.shimmerLoading(
    isLoading: Boolean,
    cornerRadius: Dp,
): Modifier {
    val color = Theme.colorScheme.background.surfaceHigh
    val transition by animateFloatAsState(
        targetValue = if (isLoading) 1f else 0f,
        animationSpec = TweenSpec(
            durationMillis = 500,
            easing = LinearOutSlowInEasing,
        ),
        label = "Shimmer Loading"
    )
    return this.drawWithContent {
        drawContent()
        drawRoundRect(
            color = color,
            alpha = transition,
            cornerRadius = CornerRadius(cornerRadius.toPx())
        )
    }
}

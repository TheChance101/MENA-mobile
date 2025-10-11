package net.thechance.mena.dukan.presentation.util.animation

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun Modifier.skeletonLoading(
    isLoading: Boolean
): Modifier {
    val color = Theme.colorScheme.background.surfaceHigh
    val transition by animateFloatAsState(
        targetValue = if (isLoading) 1f else 0f,
        animationSpec = TweenSpec(
            durationMillis = 600,
            easing = LinearOutSlowInEasing,
        ),
        label = "Skeleton Loading Transition"
    )
    return this.drawWithContent {
        drawContent()
        drawRect(
            color = color,
            alpha = transition
        )
    }
}
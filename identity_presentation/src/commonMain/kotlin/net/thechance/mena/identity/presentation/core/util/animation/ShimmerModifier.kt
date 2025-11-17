package net.thechance.mena.identity.presentation.core.util.animation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun Modifier.shimmerLoading(
    isLoading: Boolean
): Modifier {
    val finalAlpha by animateFloatAsState(
        targetValue = if (isLoading) 1f else 0f,
        animationSpec = TweenSpec(
            durationMillis = 500,
            easing = FastOutSlowInEasing,
        ),
        label = "ShimmerFinalFadeOut"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "Shimmer Infinite Animation")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerOffset"
    )

    val shimmerColor = Theme.colorScheme.background.surfaceHigh.copy(alpha = 0.6f)
    val baseColor = Theme.colorScheme.background.surfaceHigh.copy(alpha = 0.2f)

    return this.drawWithContent {
        drawContent()
        if (isLoading && finalAlpha > 0f) {
            val width = size.width
            val height = size.height

            val gradientWidth = width * 0.5f
            val startX = (width + gradientWidth * 2) * shimmerOffset - gradientWidth

            val brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    baseColor.copy(alpha = 0.3f),
                    shimmerColor.copy(alpha = 0.5f),
                    shimmerColor.copy(alpha = 0.5f),
                    baseColor.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                start = Offset(startX, height / 2f),
                end = Offset(startX + gradientWidth, height / 2f)
            )

            drawRect(
                brush = brush,
                alpha = finalAlpha
            )
        }
    }
}
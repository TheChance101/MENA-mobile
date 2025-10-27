package net.thechance.mena.dukan.presentation.util.animation

import androidx.compose.animation.core.FastOutSlowInEasing
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
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun Modifier.skeletonLoading(
    isLoading: Boolean
): Modifier {
    val finalAlpha by animateFloatAsState(
        targetValue = if (isLoading) 1f else 0f,
        animationSpec = TweenSpec(
            durationMillis = 500,
            easing = FastOutSlowInEasing,
        ),
        label = "SkeletonFinalFadeOut"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "Skeleton Infinite Shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SSkeletonShimmerAlpha"
    )

    val color = Theme.colorScheme.background.surfaceHigh

    return this.drawWithContent {
        drawContent()
         val overlayAlpha = if (isLoading) shimmerAlpha else finalAlpha
        drawRect(
            color = color,
            alpha = overlayAlpha
        )
    }
}
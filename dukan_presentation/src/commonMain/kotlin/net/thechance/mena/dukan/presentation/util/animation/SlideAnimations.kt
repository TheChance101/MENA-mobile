package net.thechance.mena.dukan.presentation.util.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset

fun slideHorizontalTransition(): ContentTransform {
    val duration = 300
    val easing = LinearEasing

    val slideSpec = tween<IntOffset>(
        durationMillis = duration,
        easing = easing
    )

    val fadeSpec = tween<Float>(
        durationMillis = duration ,
        easing = easing
    )

    return slideInHorizontally(
        animationSpec = slideSpec,
        initialOffsetX = { fullWidth -> fullWidth } // enter from right
    ) togetherWith fadeOut(
        animationSpec = fadeSpec // fade old content instead of sliding it out
    )
}
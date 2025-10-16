package net.thechance.mena.dukan.presentation.util.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith

fun fadeTransitionSpec(): ContentTransform {
    return fadeIn(
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = 100,
            easing = EaseIn
        )
    ) togetherWith fadeOut(
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = 100,
            easing = EaseOut
        )
    )
}

fun fadeCubicTransition(): ContentTransform {
    return fadeIn(
        animationSpec = tween(
            easing = EaseOutCubic
        )
    ) togetherWith fadeOut(
        animationSpec = tween(
            easing = EaseInCubic
        )
    )
}


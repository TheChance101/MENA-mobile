package net.thechance.mena.dukan.presentation.util.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseInSine
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.EaseOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset

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

fun fadeWithSlideTransition(): ContentTransform {
    return fadeIn(
        animationSpec = tween(
            easing = EaseInSine
        )
    ) + slideInHorizontally(
        animationSpec = tween(
            easing = EaseInSine
        )
    ) togetherWith fadeOut(
        animationSpec = tween(
            easing = EaseOutSine
        )
    )
}

fun fadeWithSlideHorizontalTransition(toRight: Boolean): ContentTransform {
    val duration = 700
    val easing = LinearEasing
    val animationSpec = tween<Float>(durationMillis = duration, easing = easing)
    val slideAnimationSpec = tween<IntOffset>(durationMillis = duration, easing = easing)

    return slideInHorizontally (
        animationSpec = slideAnimationSpec,
        initialOffsetX = { fullWidth -> if (toRight) fullWidth * 2 else -fullWidth * 2 }
    ) + fadeIn(
        animationSpec = animationSpec
    ) togetherWith slideOutHorizontally(
        animationSpec = slideAnimationSpec,
        targetOffsetX = { fullWidth -> if (toRight) -fullWidth * 2 else fullWidth * 2 }
    ) + fadeOut(
        animationSpec = animationSpec
    )
}



fun fadeWithSlideVerticalTransition(): ContentTransform {
    val animationSpec = tween<Float>(durationMillis = 500)

    return slideInVertically(
        animationSpec = tween(durationMillis = 500, easing = EaseOut),
        initialOffsetY = { fullHeight -> -fullHeight }
    ) + fadeIn(
        animationSpec = animationSpec
    ) togetherWith slideOutVertically(
        animationSpec = tween(durationMillis = 500, easing = EaseIn),
        targetOffsetY = { fullHeight -> -fullHeight }
    ) + fadeOut(
        animationSpec = animationSpec
    )
}
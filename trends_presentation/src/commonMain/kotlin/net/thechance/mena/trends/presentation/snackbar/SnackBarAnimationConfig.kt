package net.thechance.mena.trends.presentation.snackbar

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

data class SnackBarAnimationConfig(
    val durationMillis: Int = 500,
    val enterAnimation: EnterTransition = fadeIn(tween(durationMillis)) + slideInVertically(
        animationSpec = tween(durationMillis),
        initialOffsetY = { -it / 2 }
    ),
    val exitAnimation: ExitTransition = fadeOut(tween(durationMillis)) + slideOutVertically(
        animationSpec = tween(durationMillis),
        targetOffsetY = { -it / 2 }
    )
)

val defaultSnackBarAnimationConfig = SnackBarAnimationConfig()

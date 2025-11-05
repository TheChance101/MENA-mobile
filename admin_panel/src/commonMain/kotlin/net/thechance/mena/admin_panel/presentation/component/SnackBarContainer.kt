package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import net.thechance.mena.admin_panel.resources.ic_check_circle
import net.thechance.mena.admin_panel.resources.ic_close_circle
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource

private const val ANIMATION_DURATION = 500

@Composable
fun SnackBarContainer(
    snackBarState: SnackBarState,
    modifier: Modifier = Modifier
) {
    val targetTint =
        if (snackBarState.isSuccess) Theme.colorScheme.success else Theme.colorScheme.error

    val animatedTint by animateColorAsState(
        targetValue = targetTint,
        animationSpec = tween(durationMillis = ANIMATION_DURATION),
    )

    AnimatedVisibility(
        visible = snackBarState.isVisible,
        enter = ENTER_ANIMATION,
        exit = EXIT_ANIMATION
    ) {
        Crossfade(
            targetState = snackBarState.isSuccess,
            animationSpec = tween(ANIMATION_DURATION)
        ) { isSuccess ->
            val leadingIcon =
                if (isSuccess) Res.drawable.ic_check_circle else Res.drawable.ic_close_circle

            SnackBar(
                title = snackBarState.title ?: "",
                message = snackBarState.message ?: "",
                leadingIcon = painterResource(leadingIcon),
                modifier = modifier,
                tint = animatedTint
            )
        }
    }
}

private val ENTER_ANIMATION = fadeIn(tween(ANIMATION_DURATION)) +
        slideInHorizontally (
            animationSpec = tween(ANIMATION_DURATION),
            initialOffsetX = { it / 2 },
        )

private val EXIT_ANIMATION = fadeOut(tween(ANIMATION_DURATION)) +
        slideOutHorizontally (
            animationSpec = tween(ANIMATION_DURATION),
            targetOffsetX = { it / 2 },
        )
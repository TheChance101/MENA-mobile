package net.thechance.mena.wallet.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.balance_fetch_error_description
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.ic_check_circle
import mena.wallet_presentation.generated.resources.ic_close_circle
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.model.SnackBarState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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
        slideInVertically(
            animationSpec = tween(ANIMATION_DURATION),
            initialOffsetY = { -it / 2 },
        )

private val EXIT_ANIMATION = fadeOut(tween(ANIMATION_DURATION)) +
        slideOutVertically(
            animationSpec = tween(ANIMATION_DURATION),
            targetOffsetY = { -it / 2 },
        )

@Preview
@Composable
private fun SnackBarContainerFailPreview() {
    MenaTheme {
        SnackBarContainer(
            snackBarState = SnackBarState(
                isVisible = true,
                title = stringResource(Res.string.error),
                message = stringResource(Res.string.balance_fetch_error_description),
                isSuccess = false
            )
        )
    }
}

@Preview
@Composable
private fun SnackBarContainerSuccessPreview() {
    MenaTheme {
        SnackBarContainer(
            snackBarState = SnackBarState(
                isVisible = true,
                title = stringResource(Res.string.error),
                message = stringResource(Res.string.balance_fetch_error_description),
                isSuccess = true
            )
        )
    }
}
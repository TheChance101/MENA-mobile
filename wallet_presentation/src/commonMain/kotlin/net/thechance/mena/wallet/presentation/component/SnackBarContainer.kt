package net.thechance.mena.wallet.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.ic_check_circle
import mena.wallet_presentation.generated.resources.ic_close_circle
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.base.SnackBarState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val ANIMATION_DURATION = 500

@Composable
fun SnackBarContainer(
    snackBarState: SnackBarState,
    modifier: Modifier = Modifier
) {
    val (leadingIcon, tint) = if (snackBarState.isSuccess) {
        Res.drawable.ic_check_circle to Theme.colorScheme.success
    } else {
        Res.drawable.ic_close_circle to Theme.colorScheme.error
    }

    AnimatedVisibility(
        visible = snackBarState.isVisible,
        enter =
            fadeIn(tween(ANIMATION_DURATION)) +
                    slideInVertically(
                        animationSpec = tween(ANIMATION_DURATION),
                        initialOffsetY = { -it/2 },
                    ),
        exit =
            fadeOut(tween(ANIMATION_DURATION)) +
                    slideOutVertically(
                        animationSpec = tween(ANIMATION_DURATION),
                        targetOffsetY = { -it/2 },
                    ),
    ) {
        SnackBar(
            title = snackBarState.titleRes?.let { stringResource(it) } ?: "",
            message = snackBarState.messageRes?.let { stringResource(it) } ?: "",
            leadingIcon = painterResource(leadingIcon),
            modifier = modifier,
            tint = tint
        )
    }
}
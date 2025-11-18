package net.thechance.mena.faith.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.error
import mena.faith_presentation.generated.resources.error_icon
import mena.faith_presentation.generated.resources.success
import mena.faith_presentation.generated.resources.success_icon
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FaithSnackBar(
    message: String,
    isVisible: Boolean,
    status: SnackBarState.Status,
    modifier: Modifier = Modifier,
    animationDuration: Int = 500,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(animationDuration)) + slideInVertically(
            animationSpec = tween(animationDuration),
            initialOffsetY = { -it / 2 }
        ),
        exit = fadeOut(tween(animationDuration)) + slideOutVertically(
            animationSpec = tween(animationDuration),
            targetOffsetY = { -it / 2 }
        )
    ) {
        SnackBar(
            title = getSnackBarTitle(status),
            message = message,
            leadingIcon = getSnackBarIcon(status),
            tint = getSnackBarIconTint(status),
            modifier = modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun getSnackBarIconTint(status: SnackBarState.Status): Color {
    return if (status == SnackBarState.Status.Success)
        Theme.colorScheme.success
    else
        Theme.colorScheme.error
}

@Composable
private fun getSnackBarTitle(status: SnackBarState.Status): String {
    return if (status == SnackBarState.Status.Success)
        stringResource(Res.string.success)
    else
        stringResource(Res.string.error)
}

@Composable
private fun getSnackBarIcon(status: SnackBarState.Status): Painter {
    val icon = when (status) {
        SnackBarState.Status.Success -> Res.drawable.success_icon
        SnackBarState.Status.Error -> Res.drawable.error_icon
    }
    return painterResource(icon)
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            FaithSnackBar(
                message = "This is a success message",
                isVisible = true,
                status = SnackBarState.Status.Success
            )
        }
    }
}

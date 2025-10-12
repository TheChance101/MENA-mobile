package net.thechance.mena.trends.presentation.shared.component.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.delay
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.error
import mena.trends_presentation.generated.resources.ic_error
import mena.trends_presentation.generated.resources.ic_success
import mena.trends_presentation.generated.resources.success
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.shared.model.SnackBarStatus
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

const val ANIMATION_DURATION = 500

@Composable
internal fun TrendsSnackBar(
    message: String,
    status: SnackBarStatus,
    modifier: Modifier = Modifier,
    durationInMilliSeconds: Long = 3000,
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
        delay(durationInMilliSeconds)
        isVisible = false
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(ANIMATION_DURATION)) + slideInVertically(
            animationSpec = tween(ANIMATION_DURATION),
            initialOffsetY = { -it / 2 }
        ),
        exit = fadeOut(tween(ANIMATION_DURATION)) + slideOutVertically(
            animationSpec = tween(ANIMATION_DURATION),
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
private fun getSnackBarIconTint(status: SnackBarStatus): Color {
    return if (status == SnackBarStatus.Success)
        Theme.colorScheme.success
    else
        Theme.colorScheme.error
}

@Composable
private fun getSnackBarTitle(status: SnackBarStatus): String {
    return if (status == SnackBarStatus.Success)
        stringResource(Res.string.success)
    else
        stringResource(Res.string.error)
}

@Composable
private fun getSnackBarIcon(status: SnackBarStatus): Painter {
    val icon = when (status) {
        SnackBarStatus.Success -> Res.drawable.ic_success
        SnackBarStatus.Error -> Res.drawable.ic_error
    }
    return painterResource(icon)
}

@Preview
@Composable
private fun PreviewTrendsSnackBarSuccess() {
    MenaTheme {
        TrendsSnackBar(
            message = "Your process completed successfully",
            status = SnackBarStatus.Success
        )
    }
}

@Preview
@Composable
private fun PreviewTrendsSnackBarError() {
    MenaTheme {
        TrendsSnackBar(
            message = "Unexpected error happened",
            status = SnackBarStatus.Error
        )
    }
}
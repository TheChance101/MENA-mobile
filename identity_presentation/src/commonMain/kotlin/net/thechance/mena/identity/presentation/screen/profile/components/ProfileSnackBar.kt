package net.thechance.mena.identity.presentation.screen.profile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.delay
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error
import mena.identity_presentation.generated.resources.ic_close_circle
import mena.identity_presentation.generated.resources.ic_success
import mena.identity_presentation.generated.resources.success
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.screen.profile.SnackBarType
import net.thechance.mena.identity.presentation.screen.profile.SnackBarUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProfileSnackBar(
    snackBarState: SnackBarUiState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    displayDurationMs: Long = 3000L,
    hideAnimationDelayMs: Long = 200L
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(snackBarState.isVisible) {
        if (snackBarState.isVisible) {
            isVisible = true
            delay(displayDurationMs)
            isVisible = false
            delay(hideAnimationDelayMs)
            onDismiss()
        }
    }

    SnackBarContent(
        isVisible = isVisible,
        title = getSnackBarTitle(snackBarState.snackBarType),
        message = stringResource(snackBarState.message),
        leadingIcon = getSnackBarIcon(snackBarState.snackBarType),
        modifier = modifier
    )
}

@Composable
private fun SnackBarContent(
    isVisible: Boolean,
    title: String,
    message: String,
    leadingIcon: Painter,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
        modifier = modifier,
    ) {
        SnackBar(
            title = title,
            message = message,
            leadingIcon = leadingIcon,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing._12)
                .padding(horizontal = Theme.spacing._16)
        )
    }
}

@Composable
private fun getSnackBarTitle(snackBarType: SnackBarType): String {
    return if (snackBarType == SnackBarType.SUCCESS) {
        stringResource(Res.string.success)
    } else {
        stringResource(Res.string.error)
    }
}

@Composable
private fun getSnackBarIcon(snackBarType: SnackBarType): Painter {
    return if (snackBarType == SnackBarType.SUCCESS) {
        painterResource(Res.drawable.ic_success)
    } else {
        painterResource(Res.drawable.ic_close_circle)
    }
}
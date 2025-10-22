package net.thechance.mena.identity.presentation.components

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
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ErrorSnackBar(
    errorMessage: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    displayDurationMs: Long = 3000L,
    hideAnimationDelayMs: Long = 200L
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            isVisible = true
            delay(displayDurationMs)
            isVisible = false
            delay(hideAnimationDelayMs)
            onDismiss()
        }
    }
    SnackBarContent(
        isVisible = isVisible,
        title = stringResource(Res.string.error),
        message = errorMessage.orEmpty(),
        leadingIcon = painterResource(Res.drawable.ic_close_circle),
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
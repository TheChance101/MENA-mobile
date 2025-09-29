package net.thechance.mena.dukan.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.error
import mena.dukan_presentation.generated.resources.ic_error
import mena.dukan_presentation.generated.resources.ic_success
import mena.dukan_presentation.generated.resources.success
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SnackBar(
    snackBarUiState: SnackBarUiState,
    onDismiss: () -> Unit,
    autoDismissMillis: Long = 3000L,
    modifier: Modifier = Modifier.padding(
        start = Theme.spacing._16,
        end = Theme.spacing._16,
        top = Theme.spacing._12
    )
) {
    val messageText = stringResource(snackBarUiState.message)

    LaunchedEffect(Unit) {
        delay(autoDismissMillis)
        onDismiss()
    }

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(400)
        ) + fadeIn(animationSpec = tween(400)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    ) {
        when (snackBarUiState.snackBarType) {
            SnackBarType.SUCCESS -> {
                SnackBar(
                    title = stringResource(Res.string.success),
                    message = messageText,
                    leadingIcon = painterResource(Res.drawable.ic_success),
                    contentDescription = stringResource(Res.string.success),
                    tint = Theme.colorScheme.success,
                    modifier = modifier
                )
            }

            SnackBarType.ERROR -> {
                SnackBar(
                    title = stringResource(Res.string.error),
                    message = messageText,
                    leadingIcon = painterResource(Res.drawable.ic_error),
                    contentDescription = stringResource(Res.string.error),
                    tint = Theme.colorScheme.error,
                    modifier = modifier
                )
            }
        }
    }
}

data class SnackBarUiState(val snackBarType: SnackBarType, val message: StringResource)

enum class SnackBarType {
    SUCCESS,
    ERROR
}
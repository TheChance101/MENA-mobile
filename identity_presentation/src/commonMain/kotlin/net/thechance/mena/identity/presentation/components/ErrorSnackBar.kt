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
import kotlinx.coroutines.delay
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error
import mena.identity_presentation.generated.resources.ic_close_circle
import mena.identity_presentation.generated.resources.ic_success
import mena.identity_presentation.generated.resources.success
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.screen.addresses.SnackBarType
import net.thechance.mena.identity.presentation.screen.addresses.SnackBarUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ErrorSnackBar(
    errorMessage: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            isVisible = true
            delay(3000)
            isVisible = false
            delay(200)
            onDismiss()
        }
    }
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
        modifier = modifier,
    ) {
        SnackBar(
            title = stringResource(Res.string.error),
            message = errorMessage.orEmpty(),
            leadingIcon = painterResource(Res.drawable.ic_close_circle),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing._12)
                .padding(horizontal = Theme.spacing._16)
        )
    }

}

@Composable
internal fun AddressSnackBar(
    snackBarState: SnackBarUiState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(snackBarState.isVisible) {
        if (snackBarState.isVisible) {
            isVisible = true
            delay(3000)
            isVisible = false
            delay(200)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
        modifier = modifier,
    ) {
        SnackBar(
            title = if (
                snackBarState.snackBarType == SnackBarType.SUCCESS)
                stringResource(Res.string.success)
            else stringResource(Res.string.error),
            message = stringResource(snackBarState.message),
            leadingIcon =
                if (
                    snackBarState.snackBarType == SnackBarType.SUCCESS)
                    painterResource(Res.drawable.ic_success)
                else painterResource(
                    Res.drawable.ic_close_circle
                ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing._12)
                .padding(horizontal = Theme.spacing._16),
        )
    }
}

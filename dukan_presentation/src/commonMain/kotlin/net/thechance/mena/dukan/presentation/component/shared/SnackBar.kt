package net.thechance.mena.dukan.presentation.component.shared

import androidx.compose.runtime.Composable
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
    onClick: () -> Unit
) {
    val messageText = stringResource(snackBarUiState.message)

    when (snackBarUiState.snackBarType) {
        SnackBarType.SUCCESS -> {
            SnackBar(
                isVisible = true,
                title = stringResource(Res.string.success),
                message = messageText,
                leadingIcon = painterResource(Res.drawable.ic_success),
                tint = Theme.colorScheme.success,
                contentDescription = stringResource(Res.string.success),
                onDismiss = onDismiss,
                onClick = onClick
            )
        }

        SnackBarType.ERROR -> {
            SnackBar(
                isVisible = true,
                title = stringResource(Res.string.error),
                message = messageText,
                leadingIcon = painterResource(Res.drawable.ic_error),
                contentDescription = stringResource(Res.string.error),
                tint = Theme.colorScheme.error,
                onDismiss = onDismiss,
                onClick = onClick
            )
        }
    }
}

data class SnackBarUiState(val snackBarType: SnackBarType, val message: StringResource)

enum class SnackBarType {
    SUCCESS,
    ERROR
}
package net.thechance.mena.identity.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error
import mena.identity_presentation.generated.resources.ic_close_circle
import mena.identity_presentation.generated.resources.ic_success
import mena.identity_presentation.generated.resources.success
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.SnackBarType
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.SnackBarUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AddressSnackBar(
    snackBarState: SnackBarUiState,
    onDismiss: () -> Unit,
) {
    SnackBar(
        isVisible = snackBarState.isVisible,
        title = getSnackBarTitle(snackBarState.snackBarType),
        message = stringResource(snackBarState.message),
        leadingIcon = getSnackBarIcon(snackBarState.snackBarType),
        onDismiss = onDismiss,
    )
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
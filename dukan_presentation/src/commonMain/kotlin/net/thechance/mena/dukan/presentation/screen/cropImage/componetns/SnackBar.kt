package net.thechance.mena.dukan.presentation.screen.createDukan.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.error
import mena.dukan_presentation.generated.resources.ic_error
import mena.dukan_presentation.generated.resources.ic_success
import mena.dukan_presentation.generated.resources.success
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SnackBar(
    snackBarUiState: SnackBarUiState,
    modifier: Modifier = Modifier.padding(
        start = Theme.spacing._16,
        end = Theme.spacing._16,
        top = Theme.spacing._12
    )
) {
    when (snackBarUiState.snackBarType) {
        SnackBarType.SUCCESS -> {
            SnackBar(
                title = stringResource(Res.string.success),
                message = snackBarUiState.message,
                leadingIcon = painterResource(Res.drawable.ic_success),
                contentDescription = stringResource(Res.string.success),
                tint = Theme.colorScheme.success,
                modifier = modifier
            )
        }

        SnackBarType.ERROR -> {
            SnackBar(
                title = stringResource(Res.string.error),
                message = snackBarUiState.message,
                leadingIcon = painterResource(Res.drawable.ic_error),
                contentDescription = stringResource(Res.string.error),
                tint = Theme.colorScheme.error,
                modifier = modifier
            )
        }
    }
}

data class SnackBarUiState(val snackBarType: SnackBarType, val message: String)

enum class SnackBarType {
    SUCCESS,
    ERROR
}

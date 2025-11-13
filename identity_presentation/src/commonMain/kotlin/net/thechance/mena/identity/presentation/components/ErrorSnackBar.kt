package net.thechance.mena.identity.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
) {
    SnackBar(
        isVisible = errorMessage != null,
        title = stringResource(Res.string.error),
        message = errorMessage.orEmpty(),
        leadingIcon = painterResource(Res.drawable.ic_close_circle),
        modifier = modifier.padding(vertical = Theme.spacing._12, horizontal = Theme.spacing._16),
        onDismiss = onDismiss,
    )
}
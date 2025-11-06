package net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.confirmation_dialog_delete
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.DeleteDialogUIState
import org.jetbrains.compose.resources.stringResource

internal fun ScaffoldScope.deleteAddressDialog(
    deleteDialogUIState: DeleteDialogUIState,
    onDismissDeleteDialog: () -> Unit,
    onConfirmDeleteAddress: () -> Unit,
) {
    dialog(deleteDialogUIState.isVisible) {
        Dialog(
            isVisible = it,
            title = stringResource(deleteDialogUIState.title),
            message = stringResource(deleteDialogUIState.description),
            onDismiss = onDismissDeleteDialog,
            onCancelClick = onDismissDeleteDialog,
            actionButtons = {
                TextButton(
                    modifier = Modifier
                        .padding(
                            vertical = Theme.spacing._24,
                            horizontal = Theme.spacing._8
                        )
                        .align(Alignment.End),
                    text = stringResource(Res.string.confirmation_dialog_delete),
                    onClick = onConfirmDeleteAddress,
                    iconSize = Theme.spacing._16,
                    contentColor = Theme.colorScheme.error
                )
            }
        )
    }
}
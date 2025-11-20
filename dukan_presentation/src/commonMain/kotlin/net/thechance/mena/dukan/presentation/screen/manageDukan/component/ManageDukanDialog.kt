package net.thechance.mena.dukan.presentation.screen.manageDukan.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState.DeleteDialogState
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState.DialogType
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScaffoldScope.DeleteShelfConfirmationDialog(
    state: DeleteDialogState,
    listener: ManageDukanInteractionListener
) {
    val deleteShelfConfirmationDialog = if (state.type == DialogType.DELETE) Theme.colorScheme.error
    else Theme.colorScheme.primary.primary
    Dialog(
        title = stringResource(state.title),
        message = stringResource(state.description),
        isVisible = true,
        onDismiss = { listener.onDismissDeleteShelfConfirmationDialog() },
        actionButtons = {
            TextButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(
                        top = Theme.spacing._24,
                        bottom = Theme.spacing._8,
                        end = Theme.spacing._12
                    ),
                text = stringResource(state.type.text),
                contentColor = deleteShelfConfirmationDialog,
                onClick = {
                    if (state.type == DialogType.DISMISS) {
                        listener.onDismissDeleteShelfConfirmationDialog()
                    } else {
                        listener.onDeleteConfirmed(shelfId = state.shelfId)
                    }
                }
            )
        },
        onCancelClick = { listener.onDismissDeleteShelfConfirmationDialog() }
    )
}

fun ScaffoldScope.manageDukanDialog(
    state: ManageDukanUiState,
    listener: ManageDukanInteractionListener
) {
    dialog(isVisible = state.deleteDialog != null) {
        state.deleteDialog?.let { dialogState ->
            DeleteShelfConfirmationDialog(
                state = dialogState,
                listener = listener
            )
        }
    }
}
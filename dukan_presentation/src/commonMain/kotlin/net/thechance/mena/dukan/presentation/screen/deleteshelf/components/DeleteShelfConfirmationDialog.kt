package net.thechance.mena.dukan.presentation.screen.deleteshelf.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.deleteshelf.ConfirmDialogType
import net.thechance.mena.dukan.presentation.viewModel.deleteshelf.DeleteShelfConfirmationDialogUiState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DeleteShelfConfirmationDialog(
    state: DeleteShelfConfirmationDialogUiState,
    showDialog: Boolean,
    content:@Composable () -> Unit,
    onClick: (ConfirmDialogType) -> Unit,
) {
    if (showDialog) {
        Scaffold(
            overlays = {
                dialog(showDialog) {
                    Dialog(
                        title = state.title,
                        message = state.description,
                        buttonText = stringResource(state.type.text),
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                        onDismiss = { onClick(ConfirmDialogType.DISMISS) },
                        onActionClick = { onClick(state.type) },
                        onCancelClick = { onClick(ConfirmDialogType.DISMISS) },
                        contentColor = Theme.colorScheme.background.surfaceLow,
                    )
                }
            }
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun DeleteShelfConfirmationDialogPreview1() {
    MenaTheme {
        var showDialog by remember { mutableStateOf(true) }

        DeleteShelfConfirmationDialog(
            showDialog = showDialog,
            state = DeleteShelfConfirmationDialogUiState(
                title = "Delete shelf",
                description = "Are you sure you want to delete this shelf?",
                type = ConfirmDialogType.DELETE
            ),
            onClick = { type ->
                showDialog = when (type) {
                    ConfirmDialogType.DELETE -> {
                        false
                    }
                    ConfirmDialogType.DISMISS -> false
                }
            },
            content = {}
        )
    }
}
@Preview
@Composable
private fun DeleteShelfConfirmationDialogPreview2() {
    MenaTheme {
        var showDialog by remember { mutableStateOf(true) }

        DeleteShelfConfirmationDialog(
            showDialog = showDialog,
            state = DeleteShelfConfirmationDialogUiState(
                title = "Unable to Delete Shelf",
                description = "This shelf has a products inside it, you can’t delete it until remove products.",
                type = ConfirmDialogType.DISMISS
            ),
            onClick = { type ->
                showDialog = when (type) {
                    ConfirmDialogType.DELETE -> {
                        false
                    }
                    ConfirmDialogType.DISMISS -> false
                }
            },
            content = {}
        )
    }
}

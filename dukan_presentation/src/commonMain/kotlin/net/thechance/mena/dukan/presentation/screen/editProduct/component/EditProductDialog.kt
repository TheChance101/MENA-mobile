package net.thechance.mena.dukan.presentation.screen.editProduct.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.delete
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.editProduct.EditProductInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.editProduct.EditProductUiState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScaffoldScope.DeleteProductConfirmationDialog(
    state: EditProductUiState.DeleteDialogState,
    listener: EditProductInteractionListener
) {
    Dialog(
        title = stringResource(state.title),
        message = stringResource(state.description),
        isVisible = true,
        onDismiss = { listener.onDismissDeleteDialog() },
        actionButtons = {
            TextButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = Theme.spacing._24, bottom = Theme.spacing._12),
                text = stringResource(Res.string.delete),
                contentColor = Theme.colorScheme.error,
                onClick = { listener.onDeleteConfirmed() }
            )
        },
        onCancelClick = { listener.onDismissDeleteDialog() }
    )
}

fun ScaffoldScope.editProductDialog(
    state: EditProductUiState,
    listener: EditProductInteractionListener
) {
    dialog(isVisible = state.deleteDialog != null) {
        state.deleteDialog?.let { dialogState ->
            DeleteProductConfirmationDialog(
                state = dialogState,
                listener = listener
            )
        }
    }
}

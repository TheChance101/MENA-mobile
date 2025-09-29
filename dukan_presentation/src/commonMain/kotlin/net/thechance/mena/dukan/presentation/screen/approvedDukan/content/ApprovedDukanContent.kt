package net.thechance.mena.dukan.presentation.screen.approvedDukan.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.ic_add_bold
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.my_dukan
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.SnackBar
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewApprovedDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ConfirmDialogType
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.DeleteShelfConfirmationDialogUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ApprovedDukanContent(
    state: ApprovedDukanUiState,
    listener: ApprovedDukanInteractionListener,
    deletedShelfId: String? = null
) {
    OnSystemBackPressed(listener::onBackButtonClicked)

    Scaffold(
        overlays = {
            dialog(state.showDeleteConfirmationDialog) {
                state.deleteShelfConfirmationDialogUiState?.let {
                    DeleteShelfConfirmationDialog(
                        state = it,
                        deletedShelfId = deletedShelfId,
                        listener = listener
                    )
                }
            }
        },
        topBar = {
            AppBar(
                title = stringResource(Res.string.my_dukan),
                onLeadingClick = listener::onBackButtonClicked,
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._16,
                    vertical = Theme.spacing._8
                ),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_arrow),
                        tint = Theme.colorScheme.shadePrimary
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.shelves.isNotEmpty()) {
                ApprovedDukanHeader(
                    state = state,
                    listener = listener
                )
            }

            if (state.shelves.isEmpty() || state.products.isEmpty()) {
                Spacer(modifier = Modifier.weight(1f))
            }

            ApprovedDukanProducts(
                state = state,
                onProductClick = listener::onProductClick
            )

            Spacer(modifier = Modifier.weight(1f))

            FabButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = Theme.spacing._16, bottom = Theme.spacing._24),
                onClick = listener::onAddShelfClicked,
                painter = painterResource(Res.drawable.ic_add_bold)
            )

        }
    }

    state.snackBarState?.let { snackBarState ->
        SnackBar(
            snackBarUiState = snackBarState,
            onDismiss = listener::onDismissSnackBar
        )
    }
}

@Composable
private fun ScaffoldScope.DeleteShelfConfirmationDialog(
    state: DeleteShelfConfirmationDialogUiState,
    deletedShelfId: String?,
    listener: ApprovedDukanInteractionListener
) {
    Dialog(
        title = stringResource(state.title),
        message = stringResource(state.description),
        buttonText = stringResource(state.type.text),
        onDismiss = { listener.onDismissDeleteShelfConfirmationDialog() },
        onActionClick = {
            if (state.type == ConfirmDialogType.DISMISS) {
                listener.onDismissDeleteShelfConfirmationDialog()
            } else {
                deletedShelfId?.let { shelfId ->
                    listener.deleteShelf(shelfId = shelfId)
                }
            }
        },
        onCancelClick = { listener.onDismissDeleteShelfConfirmationDialog() }
    )
}


@Preview
@Composable
private fun ApprovedDukanContentPreview() {
    MenaTheme {
        ApprovedDukanContent(
            state = ApprovedDukanUiState(),
            listener = PreviewApprovedDukanInteractionListener
        )
    }
}

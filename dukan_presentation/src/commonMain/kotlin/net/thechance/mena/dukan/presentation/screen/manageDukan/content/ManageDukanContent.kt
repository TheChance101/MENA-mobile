package net.thechance.mena.dukan.presentation.screen.manageDukan.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
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
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.SnackBar
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.stubPreviews.FakeProductPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewManageDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ConfirmDialogType
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.DeleteShelfConfirmationDialogUiState
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ProductUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ManageDukanContent(
    state: ManageDukanUiState,
    listener: ManageDukanInteractionListener,
    pager: Pager<Int, ProductUiState>
) {
    OnSystemBackPressed(listener::onBackButtonClicked)

    Scaffold(
        overlays = { manageDukanDialog(state, listener) },
        topBar = { ManageDukanAppBar(listener) },
        snakeBar = { ManageDukanSnackbar(state, listener) }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                ManageDukanHeader(
                    state = state,
                    listener = listener
                )

                if (state.shelves.isEmpty() || state.products.items.isEmpty()) {
                    Spacer(modifier = Modifier.weight(1f))
                }
                ManageDukanProducts(
                    state = state,
                    onProductClick = listener::onProductClick,
                    pager = pager
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            FabButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Theme.spacing._16, bottom = Theme.spacing._24),
                onClick = listener::onAddShelfClicked,
                painter = painterResource(Res.drawable.ic_add_bold)
            )
        }
    }
}

private fun ScaffoldScope.manageDukanDialog(
    state: ManageDukanUiState,
    listener: ManageDukanInteractionListener
) {
    dialog(state.showDeleteConfirmationDialog) {
        state.deleteShelfConfirmationDialogUiState?.let {
            DeleteShelfConfirmationDialog(
                state = state.deleteShelfConfirmationDialogUiState,
                deletedShelfId = state.deleteShelfConfirmationDialogUiState.shelfId,
                listener = listener
            )
        }
    }
}

@Composable
private fun ManageDukanAppBar(listener: ManageDukanInteractionListener) {
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

@Composable
private fun ManageDukanSnackbar(
    state: ManageDukanUiState,
    listener: ManageDukanInteractionListener
) {
    AnimatedContent(
        targetState = state.snackBarState != null,
        transitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up
            )
        }
    ) {
        if (it) {
            state.snackBarState?.let {
                SnackBar(
                    snackBarUiState = state.snackBarState,
                    onDismiss = listener::onDismissSnackBar
                )
            }
        }
    }
}

@Composable
private fun ScaffoldScope.DeleteShelfConfirmationDialog(
    state: DeleteShelfConfirmationDialogUiState,
    deletedShelfId: String?,
    listener: ManageDukanInteractionListener
) {
    Dialog(
        title = stringResource(state.title),
        isVisible = state.isDialogVisible,
        message = stringResource(state.description),
        onDismiss = { listener.onDismissDeleteShelfConfirmationDialog() },
        actionButtons = {
            TextButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = Theme.spacing._24, bottom = Theme.spacing._8),
                text = stringResource(state.type.text),
                onClick = {
                    if (state.type == ConfirmDialogType.DISMISS) {
                        listener.onDismissDeleteShelfConfirmationDialog()
                    } else {
                        deletedShelfId?.let { shelfId ->
                            listener.onDeleteConfirmed(shelfId = shelfId)
                        }
                    }
                }
            )
        },
        onCancelClick = { listener.onDismissDeleteShelfConfirmationDialog() }
    )
}


@Preview
@Composable
private fun ManageDukanContentPreview() {
    MenaTheme {
        ManageDukanContent(
            state = ManageDukanUiState(),
            listener = PreviewManageDukanInteractionListener,
            pager = Pager(
                config = PagingConfig(),
                pagingSourceFactory = { FakeProductPagingSource() }
            )
        )
    }
}

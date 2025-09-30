package net.thechance.mena.dukan.presentation.screen.approvedDukan

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.my_dukan
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.approvedDukan.content.ApprovedDukanContent
import net.thechance.mena.dukan.presentation.screen.manageShelf.ManageShelfArgs
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanEffect
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanViewModel
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ConfirmDialogType
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.DeleteShelfConfirmationDialogUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ApprovedDukanScreen(
    viewModel: ApprovedDukanViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    val deletedShelfId = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>(ManageShelfArgs.deletedShelfId, null)
        ?.collectAsStateWithLifecycle()

    LaunchedEffect(deletedShelfId?.value != null) {
        viewModel.onShowDeleteShelfConfirmationDialog()
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.remove<String>(ManageShelfArgs.deletedShelfId)
    }

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            ApprovedDukanEffect.NavigateBack -> navController.popBackStack()

            ApprovedDukanEffect.NavigateToAddShelf -> navController.navigate(
                DukanRoute.CreateShelfScreenRoute
            )

            ApprovedDukanEffect.NavigateToEditShelf -> {
            }

            ApprovedDukanEffect.NavigateToAddProduct -> {
            }

            ApprovedDukanEffect.NavigateToProductDetails -> {
            }
        }
    }
    Scaffold(
        overlays = {
            dialog(state.showDeleteConfirmationDialog) {
                state.deleteShelfConfirmationDialogUiState?.let {
                    DeleteShelfConfirmationDialog(
                        state = it,
                        deletedShelfId = deletedShelfId?.value,
                        listener = viewModel
                    )
                }
            }
        },
        topBar = {
            AppBar(
                title = stringResource(Res.string.my_dukan),
                onLeadingClick = viewModel::onBackButtonClicked,
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
        ApprovedDukanContent(
            state = state,
            listener = viewModel,
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


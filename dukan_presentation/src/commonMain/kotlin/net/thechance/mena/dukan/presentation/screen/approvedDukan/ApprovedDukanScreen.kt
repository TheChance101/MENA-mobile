package net.thechance.mena.dukan.presentation.screen.approvedDukan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.approvedDukan.content.ApprovedDukanContent
import net.thechance.mena.dukan.presentation.screen.manageShelf.ManageShelfArgs
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanEffect
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanViewModel
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ConfirmDialogType
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

    LaunchedEffect(deletedShelfId?.value) {
        val shelfId = deletedShelfId?.value
        if (shelfId != null) {
            viewModel.onShowDeleteShelfConfirmationDialog()
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<String>(ManageShelfArgs.deletedShelfId)
        }
    }

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            ApprovedDukanEffect.NavigateBack -> navController.popBackStack()

            ApprovedDukanEffect.NavigateToAddShelf -> navController.navigate(
                DukanRoute.CreateShelfScreenRoute
            )

            ApprovedDukanEffect.NavigateToEditShelf -> {
                // TODO: Navigate to edit and delete shelf screen
            }

            ApprovedDukanEffect.NavigateToAddProduct -> {
                // TODO: Navigate to add product screen
            }

            ApprovedDukanEffect.NavigateToProductDetails -> {
                // TODO: Navigate to product details screen
            }
        }
    }
    ApprovedDukanDialog(
        state = state,
        listener = viewModel,
        deletedShelfId = deletedShelfId?.value
    ) {
        ApprovedDukanContent(
            state = state,
            listener = viewModel,
        )
    }
}

@Composable
private fun ApprovedDukanDialog(
    state: ApprovedDukanUiState,
    listener: ApprovedDukanInteractionListener,
    deletedShelfId: String? = null,
    content: @Composable () -> Unit
) {
    Scaffold(
        overlays = {
            dialog(state.showDeleteConfirmationDialog) {
                state.deleteShelfConfirmationDialogUiState?.let {
                    Dialog(
                        title = stringResource(state.deleteShelfConfirmationDialogUiState.title),
                        message = stringResource(state.deleteShelfConfirmationDialogUiState.description),
                        buttonText = stringResource(state.deleteShelfConfirmationDialogUiState.type.text),
                        onDismiss = { listener.onDismissDeleteShelfConfirmationDialog() },
                        onActionClick = {
                            if (state.deleteShelfConfirmationDialogUiState.type == ConfirmDialogType.DISMISS)
                                listener.onDismissDeleteShelfConfirmationDialog()
                            else {
                                deletedShelfId?.let { shelfId ->
                                    listener.deleteShelf(shelfId = shelfId)
                                }
                            }
                        },
                        onCancelClick = { listener.onDismissDeleteShelfConfirmationDialog() }
                    )
                }
            }
        }
    )
    {
        content()
    }
}


package net.thechance.mena.admin_panel.presentation.screen.users_management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.admin_panel.presentation.component.EmptySearchState
import net.thechance.mena.admin_panel.presentation.component.PanelScaffold
import net.thechance.mena.admin_panel.presentation.screen.users_management.component.BlockUserDialog
import net.thechance.mena.admin_panel.presentation.component.SearchBar
import net.thechance.mena.admin_panel.presentation.component.SnackBarContainer
import net.thechance.mena.admin_panel.presentation.screen.users_management.component.UsersListContent
import net.thechance.mena.admin_panel.presentation.screen.users_management.component.UsersLoadingIndicator
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.search_hint
import net.thechance.mena.admin_panel.resources.users_management
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UsersManagementScreen(viewModel: UsersManagementViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    UsersManagementScreenContent(state = state, listener = viewModel)
}

@Composable
private fun UsersManagementScreenContent(
    state: UsersManagementScreenState,
    listener: UsersManagementInteractionListener
) {
    PanelScaffold(
        topBar = { UsersManagementTopBar() },
        overlays = {
            dialog(state.isBlockDialogShown) {
                BlockUserDialog(
                    isVisible = it,
                    onDismiss = listener::onBlockDialogDismissed,
                    onConfirmBlock = listener::onBlockConfirmed
                )
            }
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        errorState = state.errorState,
        onRetry = listener::onRetryClicked
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                value = state.query,
                hint = stringResource(Res.string.search_hint),
                onValueChange = listener::onSearchQueryChanged,
                onClearQueryClicked = listener::onClearQueryClicked,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .align(Alignment.End)
                    .padding(16.dp)
            )

            when {
                state.isLoading && state.users.isEmpty()-> UsersLoadingIndicator()

                state.users.isEmpty() ->{
                    if (state.query.isNotEmpty()) {
                        UsersSearchEmptyState()
                    } else{
                        UsersSearchEmptyState() /*TODO() change to users empty state*/
                    }
                }

                else ->{
                    UsersListContent(
                        listener = listener,
                        state = state,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun UsersManagementTopBar() {
    AppBar(
        title = stringResource(Res.string.users_management),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
        modifier = Modifier.background(Theme.colorScheme.background.surfaceLow)
    )
}

@Composable
private fun UsersSearchEmptyState() {
    EmptySearchState(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = -(76.dp))
    )
}

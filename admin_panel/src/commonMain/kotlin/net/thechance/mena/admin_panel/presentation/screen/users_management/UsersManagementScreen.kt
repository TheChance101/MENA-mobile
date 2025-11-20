package net.thechance.mena.admin_panel.presentation.screen.users_management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.admin_panel.presentation.component.AdminConfirmationDialog
import net.thechance.mena.admin_panel.presentation.component.EmptySearchState
import net.thechance.mena.admin_panel.presentation.component.PanelScaffold
import net.thechance.mena.admin_panel.presentation.component.SearchBar
import net.thechance.mena.admin_panel.presentation.component.SnackBarContainer
import net.thechance.mena.admin_panel.presentation.component.StatePlaceholder
import net.thechance.mena.admin_panel.presentation.screen.users_management.component.UsersManagementTableContent
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.block
import net.thechance.mena.admin_panel.resources.block_user
import net.thechance.mena.admin_panel.resources.block_user_confirmation_message
import net.thechance.mena.admin_panel.resources.empty_users
import net.thechance.mena.admin_panel.resources.ic_block
import net.thechance.mena.admin_panel.resources.ic_user_block
import net.thechance.mena.admin_panel.resources.no_users_yet
import net.thechance.mena.admin_panel.resources.search_hint
import net.thechance.mena.admin_panel.resources.users_management
import net.thechance.mena.admin_panel.resources.users_will_appear
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
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
        topBar = { UsersManagementScreenTopBar() },
        overlays = {
            dialog(state.isBlockDialogShown) {
                AdminConfirmationDialog(
                    isVisible = it,
                    onDismiss = listener::onBlockDialogDismissed,
                    dialogIcon = painterResource(Res.drawable.ic_user_block),
                    confirmationIcon = painterResource(Res.drawable.ic_block),
                    title = stringResource(Res.string.block_user),
                    description = stringResource(Res.string.block_user_confirmation_message),
                    confirmationButtonText = stringResource(Res.string.block),
                    onConfirm = listener::onBlockConfirmed,
                    modifier = Modifier.widthIn(max = 400.dp)
                )
            }
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        errorState = state.errorState,
        isLoading = state.isInitialLoading,
        onRetry = listener::onRetryClicked
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                value = state.query,
                hint = stringResource(Res.string.search_hint),
                onValueChange = listener::onSearchQueryChanged,
                onClearQueryClicked = listener::onClearQueryClicked,
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .align(Alignment.End)
                    .padding(16.dp)
            )

            when {
                state.users.isEmpty() && !state.isLoading -> {
                    if (state.query.isNotEmpty()) {
                        EmptySearchState(modifier = Modifier.fillMaxSize().offset(y = -(76.dp)))

                    } else {
                        EmptyUsersState()
                    }
                }

                else -> {
                    UsersManagementTableContent(
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
private fun EmptyUsersState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        StatePlaceholder(
            image = painterResource(Res.drawable.empty_users),
            title = stringResource(Res.string.no_users_yet),
            description = stringResource(Res.string.users_will_appear),
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .offset(y = -(76.dp))
        )
    }
}

@Composable
private fun UsersManagementScreenTopBar() {
    AppBar(
        title = stringResource(Res.string.users_management),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
        modifier = Modifier.background(Theme.colorScheme.background.surfaceLow)
    )
}
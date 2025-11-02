package net.thechance.mena.admin_panel.presentation.screen.users_management


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.admin_panel.presentation.component.ErrorView
import net.thechance.mena.admin_panel.presentation.component.PanelScaffold
import net.thechance.mena.admin_panel.presentation.component.StatePlaceholder
import net.thechance.mena.admin_panel.presentation.screen.users_management.component.BlockUserDialog
import net.thechance.mena.admin_panel.presentation.screen.users_management.component.SearchBar
import net.thechance.mena.admin_panel.presentation.screen.users_management.component.UsersListContent
import net.thechance.mena.admin_panel.presentation.utils.ObserveAsEffect
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.img_search_empty
import net.thechance.mena.admin_panel.resources.no_search_result
import net.thechance.mena.admin_panel.resources.no_search_result_description
import net.thechance.mena.admin_panel.resources.users_management
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UsersManagementScreen(viewModel: UsersManagementViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onUsersManagementEffect(effect = effect)
        }
    )
    UsersManagementScreenContent(state = state, listener = viewModel)
}

@Composable
private fun UsersManagementScreenContent(
    state: UsersManagementScreenState,
    listener: UsersManagementInteractionListener
) {
    PanelScaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.users_management),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
                modifier = Modifier.background(Theme.colorScheme.background.surfaceLow)
            )
        },
        overlays = {
            dialog(state.showBlockDialog) {
                BlockUserDialog(
                    onDismiss = listener::onDismissBlockDialog,
                    onConfirmBlock = listener::onConfirmBlock
                )
            }
        },
        errorState = state.errorState,
        isLoading = state.isLoading,
        onRetry = listener::onRetryClicked
    ) {
        when {
            state.errorState != null ->
                ErrorView(onRetry = listener::onRetryClicked)

            state.users.isEmpty() && state.query.isNotEmpty() -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchBar(
                        value = state.query,
                        onValueChange = { newQuery ->
                            listener.onSearchQueryChanged(newQuery)
                        },
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                    )
                    StatePlaceholder(
                        image = painterResource(Res.drawable.img_search_empty),
                        title = stringResource(Res.string.no_search_result),
                        description = stringResource(Res.string.no_search_result_description),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchBar(
                        value = state.query,
                        onValueChange = { newQuery ->
                            listener.onSearchQueryChanged(newQuery)
                        },
                        modifier = Modifier.padding(16.dp)
                    )
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

private fun onUsersManagementEffect(
    effect: UsersManagementEffect,
) {
    when (effect) {
        is UsersManagementEffect.ShowConfirmationMessage -> {}
    }
}
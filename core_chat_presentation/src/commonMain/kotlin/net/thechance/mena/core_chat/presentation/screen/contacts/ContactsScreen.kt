package net.thechance.mena.core_chat.presentation.screen.contacts

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.SharedFlow
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.contacts_title
import mena.core_chat_presentation.generated.resources.could_not_load_contacts
import mena.core_chat_presentation.generated.resources.ic_arrow_left
import mena.core_chat_presentation.generated.resources.ic_resync
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.presentation.components.ErrorView
import net.thechance.mena.core_chat.presentation.components.LoadingView
import net.thechance.mena.core_chat.presentation.components.snackBarHost.LocalSnackBarHostController
import net.thechance.mena.core_chat.presentation.navigation.ChatDetailsRoute
import net.thechance.mena.core_chat.presentation.navigation.LocalNavController
import net.thechance.mena.core_chat.presentation.navigation.SyncContactsRoute
import net.thechance.mena.core_chat.presentation.screen.contacts.components.ContactsList
import net.thechance.mena.core_chat.presentation.screen.syncContacts.IS_SYNC_SUCCESS
import net.thechance.mena.core_chat.presentation.utils.EffectHandler
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun ContactsScreen(viewModel: ContactsViewModel = koinViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val effect = viewModel.effect

    observeSyncSuccess(onSyncSuccess = viewModel::onSyncSuccess)

    EffectsHandler(effects = effect)

    ContactsContent(
        state = state,
        interactionListener = viewModel
    )
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun ContactsContent(
    state: ContactsScreenState,
    interactionListener: ContactsScreenInteractionListener
) {
    val contacts = state.contacts.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            AppBar(
                modifier = Modifier,
                title = stringResource(Res.string.contacts_title),
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._12,
                    vertical = Theme.spacing._8
                ),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        modifier = Modifier.size(20.dp),
                        contentDescription = null,
                        tint = Theme.colorScheme.primary.primary,
                    )
                },
                onLeadingClick = interactionListener::onBackClicked,
                trailingContent = {
                    AppBarOptionContainer(
                        badgeColor = Theme.colorScheme.primary.primary,
                        onClick = interactionListener::onReSyncClicked
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_resync),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Theme.colorScheme.shadePrimary,
                        )
                    }
                }
            )
        }
    ) {

        AnimatedContent(
            targetState = contacts.loadState.refresh to (contacts.itemCount == 0),
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { (loadState, isEmptyList) ->
            if (loadState is LoadState.Loading && isEmptyList) {
                LoadingView()

            } else if (loadState is LoadState.Error && isEmptyList) {
                ErrorView(
                    title = stringResource(Res.string.something_went_wrong),
                    message = stringResource(Res.string.could_not_load_contacts),
                    onRetry = interactionListener::onRefreshContactsClicked
                )

            } else {
                ContactsList(
                    contacts = contacts,
                    onContactClick = interactionListener::onContactClicked
                )
            }
        }
    }
}

@Composable
private fun EffectsHandler(
    effects: SharedFlow<ContactsScreenEffect>,
) {
    val snackBarHostController = LocalSnackBarHostController.current
    val navController = LocalNavController.current

    EffectHandler(effects = effects) { effect ->
        when (effect) {
            ContactsScreenEffect.NavigateBack -> {
                navController.popBackStack()
            }

            is ContactsScreenEffect.NavigateToChat -> {
                navController.navigate(
                    ChatDetailsRoute(
                        chatId = effect.chatId,
                        chatName = effect.chatName
                    )
                )
            }

            ContactsScreenEffect.NavigateToSyncContacts -> {
                navController.navigate(SyncContactsRoute(forceSync = true))
            }

            is ContactsScreenEffect.ShowSnackBar -> {
                snackBarHostController.showSnackBar(effect.snackBarData)
            }
        }
    }
}

@Composable
private fun observeSyncSuccess(
    onSyncSuccess: () -> Unit
) {
    val navController = LocalNavController.current

    val stateFlow = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow(IS_SYNC_SUCCESS, false)

    val isSyncedState = stateFlow?.collectAsState(initial = false)
    val isSynced = isSyncedState?.value == true

    LaunchedEffect(isSynced) {
        if (isSynced) {
            onSyncSuccess()
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set(IS_SYNC_SUCCESS, false)
        }
    }
}
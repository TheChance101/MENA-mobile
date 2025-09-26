package net.thechance.mena.core_chat.presentation.screen.contacts

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.contacts_title
import mena.core_chat_presentation.generated.resources.could_not_load_contacts
import mena.core_chat_presentation.generated.resources.ic_arrow_left
import mena.core_chat_presentation.generated.resources.ic_resync
import mena.core_chat_presentation.generated.resources.loading
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.presentation.components.ErrorView
import net.thechance.mena.core_chat.presentation.screen.contacts.components.ContactsList
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ContactsScreen(viewModel: ContactsViewModel = koinViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    ContactsContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
private fun ContactsContent(
    state: ContactsScreenState,
    interactionListener: ContactsScreenInteractionListener
) {
    val contacts = state.contacts.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Theme.colorScheme.background.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
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
            onLeadingClick = interactionListener::onBackClick,
            trailingContent = {
                AppBarOptionContainer(
                    badgeColor = Theme.colorScheme.primary.primary,
                    onClick = interactionListener::onReSyncClick
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
        AnimatedContent(
            targetState = contacts.loadState.refresh,
            modifier = Modifier.fillMaxSize().padding(horizontal = Theme.spacing._16),
            contentAlignment = Alignment.Center
        ) { loadState ->
            when (loadState) {
                is LoadState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.loading),
                            style = Theme.typography.title.small
                        )
                    }
                }

                is LoadState.Error -> {
                    ErrorView(
                        title = stringResource(Res.string.something_went_wrong),
                        message = stringResource(Res.string.could_not_load_contacts),
                        onRetry = interactionListener::onRefreshContacts
                    )
                }

                else -> ContactsList(
                    contacts = contacts,
                    onContactClick = interactionListener::onContactClick
                )
            }
        }
    }
}
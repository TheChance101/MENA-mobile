package net.thechance.mena.core_chat.presentation.screen.shareAyaScreen.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.could_not_load_contacts
import mena.core_chat_presentation.generated.resources.search_by_name
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.presentation.components.ErrorView
import net.thechance.mena.core_chat.presentation.components.LoadingView
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactUiState
import net.thechance.mena.core_chat.presentation.screen.contacts.components.ContactsList
import net.thechance.mena.core_chat.presentation.screen.shareAyaScreen.ShareMessageInteractionListener
import net.thechance.mena.core_chat.presentation.screen.shareAyaScreen.ShareMessageScreenState
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun SearchContactToShareView(
    contacts: LazyPagingItems<ContactUiState>,
    interactions: ShareMessageInteractionListener,
    state: ShareMessageScreenState
) {
    Column {
        SearchBar(
            value = state.searchQuery,
            hint = stringResource(Res.string.search_by_name),
            onValueChange = { query -> interactions.onSearchQueryChanged(query = query) },
            onClearQueryClicked = interactions::onClearQueryClicked,
            modifier = Modifier.padding(
                horizontal = Theme.spacing._16,
                vertical = Theme.spacing._8
            )
        )
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
                    onRetry = interactions::onClearQueryClicked
                )
            } else {
                ContactsList(
                    contacts = contacts,
                    onContactClick = interactions::onContactClicked
                )
            }
        }
    }
}
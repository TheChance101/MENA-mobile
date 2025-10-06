package net.thechance.mena.core_chat.presentation.screen.contacts.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_warning
import mena.core_chat_presentation.generated.resources.no_contacts_message
import mena.core_chat_presentation.generated.resources.refresh_contacts_message
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactUiState
import net.thechance.mena.core_chat.presentation.screen.syncContacts.components.PhoneIcon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ContactsList(
    contacts: LazyPagingItems<ContactUiState>,
    onContactClick: (Uuid?) -> Unit,
) {
    AnimatedContent(
        targetState = Pair((contacts.itemCount == 0), contacts.loadState.refresh),
        modifier = Modifier.fillMaxSize().padding(horizontal = Theme.spacing._16),
        contentAlignment = Alignment.Center
    ) { (isEmpty, loadState) ->
        if (isEmpty && loadState != LoadState.Loading) {
            EmptyContactsView()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._16),
                contentPadding = PaddingValues(vertical = Theme.spacing._8)
            ) {
                items(
                    count = contacts.itemCount,
                    key = { index -> index }
                ) { index ->
                    val contact = contacts[index]
                    contact?.let {
                        ContactItem(contact = it, onContactClick = { onContactClick(contact.menaUserId) })
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyContactsView() {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = Theme.spacing._24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier.padding(bottom = Theme.spacing._12)
        ) {
            PhoneIcon()
            Icon(
                painter = painterResource(Res.drawable.ic_warning),
                contentDescription = null,
                modifier = Modifier.size(28.6.dp)
                    .then(
                        if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
                            Modifier.align(Alignment.TopStart).offset(y = 23.dp, x = 3.dp)
                        } else {
                            Modifier.align(Alignment.TopEnd).offset(y = 23.dp, x = (-3).dp)
                        }
                    )
            )
        }
        Text(
            text = stringResource(Res.string.no_contacts_message),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(Res.string.refresh_contacts_message),
            modifier = Modifier.padding(top = Theme.spacing._2),
            textAlign = TextAlign.Center,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )
    }
}
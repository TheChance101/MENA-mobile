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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_warning
import mena.core_chat_presentation.generated.resources.no_contacts_message
import mena.core_chat_presentation.generated.resources.refresh_contacts_message
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactUi
import net.thechance.mena.core_chat.presentation.screen.syncContacts.components.PhoneIcon
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ContactsList(
    contacts: List<ContactUi>
) {
    AnimatedContent(
        targetState = contacts.isEmpty(),
        modifier = Modifier.fillMaxSize()
    ) { isEmpty ->
        if (isEmpty) EmptyContactsColumn()
        else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Theme.spacing._16),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._16),
                contentPadding = PaddingValues(vertical = Theme.spacing._8)
            ) {
                items(contacts.size) { contact ->
                    ContactItem(
                        contact = contacts[contact],
                        onContactClick = { /* //TODO: navigate to chat screen */ },
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyContactsColumn() {
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
            PhoneIcon(
            )
            MenaIcon(
                painter = painterResource(Res.drawable.ic_warning),
                contentDescription = null,
                modifier = Modifier.size(28.6.dp)
                    .align(Alignment.TopEnd)
                    .offset(y = 23.dp, x = (-3).dp)
            )
        }
        MenaText(
            text = stringResource(Res.string.no_contacts_message),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        MenaText(
            text = stringResource(Res.string.refresh_contacts_message),
            modifier = Modifier.padding(top = Theme.spacing._2, bottom = Theme.spacing._12),
            textAlign = TextAlign.Center,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )
    }
}
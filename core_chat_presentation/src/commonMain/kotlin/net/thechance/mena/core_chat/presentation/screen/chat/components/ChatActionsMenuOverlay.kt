package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.actions
import mena.core_chat_presentation.generated.resources.delete
import mena.core_chat_presentation.generated.resources.delete_chat
import mena.core_chat_presentation.generated.resources.delete_chat_confirmation_message
import mena.core_chat_presentation.generated.resources.ic_delete
import net.thechance.mena.core_chat.presentation.screen.chat.ActionsMenuInteractionListener
import net.thechance.mena.designsystem.presentation.component.dialog.BasicDialog
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun ScaffoldScope.chatActionsMenuDialog(
    showChatActionsDialog: Boolean,
    showConfirmDeleteChatDialog: Boolean,
    actionsMenuInteractionListener: ActionsMenuInteractionListener
) {
    dialog(isVisible = showChatActionsDialog) {
        BasicDialog(
            isVisible = showChatActionsDialog,
            onDismiss = actionsMenuInteractionListener::onChatActionsMenuDialogDismissed,
            onCancelClick = actionsMenuInteractionListener::onChatActionsMenuDialogDismissed,
            contentPadding = PaddingValues(Theme.spacing._12),
            actionButtons = {
                ActionMenuItem(
                    icon = painterResource(Res.drawable.ic_delete),
                    text = stringResource(Res.string.delete_chat),
                    onClick = actionsMenuInteractionListener::onDeleteChatClicked,
                    contentColor = Theme.colorScheme.error,
                    modifier = Modifier.padding(bottom = Theme.spacing._12)
                )
            }
        ) {
            Text(
                text = stringResource(Res.string.actions),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = Theme.spacing._12)
            )
        }
    }
    dialog(isVisible = showConfirmDeleteChatDialog) {
        BasicDialog(
            isVisible = showConfirmDeleteChatDialog,
            onDismiss = actionsMenuInteractionListener::onConfirmDeleteChatDialogDismissed,
            onCancelClick = actionsMenuInteractionListener::onConfirmDeleteChatDialogDismissed,
            contentPadding = PaddingValues(Theme.spacing._12),
        ) {
            ConfirmDeleteChatContent(onConfirmDeleteChatClicked = actionsMenuInteractionListener::onConfirmDeleteChatClicked)
        }
    }
}

@Composable
private fun ConfirmDeleteChatContent(onConfirmDeleteChatClicked: () -> Unit) {
    Column {
        Text(
            text = stringResource(Res.string.delete_chat),
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = Theme.spacing._12)
        )
        Text(
            text = stringResource(Res.string.delete_chat_confirmation_message),
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._12)
        )
        Text(
            text = stringResource(Res.string.delete),
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.error,
            modifier = Modifier
                .padding(vertical = Theme.spacing._12)
                .padding(end = 8.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onConfirmDeleteChatClicked
                )
                .align(Alignment.End)
        )
    }
}
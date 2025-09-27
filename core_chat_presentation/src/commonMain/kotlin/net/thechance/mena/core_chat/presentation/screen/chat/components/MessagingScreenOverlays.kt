package net.thechance.mena.core_chat.presentation.screen.chat.components

import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.confirm_delete_chat_message
import mena.core_chat_presentation.generated.resources.delete
import mena.core_chat_presentation.generated.resources.delete_chat
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import org.jetbrains.compose.resources.stringResource

fun ScaffoldScope.messagingScreenOverlays(
    showChatActionsDialog: Boolean,
    showResendMessageDialog: Boolean,
    showDeleteChatDialog: Boolean,
    onDeleteChatClick: () -> Unit = { },
    onConfirmDeleteChatClick: () -> Unit = { },
    onDeleteFailedMessageClick: () -> Unit = { },
    onResendFailedMessageClick: () -> Unit = { },
    onDismissChatActionsDialog: () -> Unit = { },
    onDismissDeleteChatDialog: () -> Unit = { },
    onDismissResendMessageDialog: () -> Unit = { },

    ) {
    dialog(showChatActionsDialog) {
        ChatActionsDialog(
            onDeleteChatClick = onDeleteChatClick,
            onDismiss = onDismissChatActionsDialog
        )
    }

    dialog(showDeleteChatDialog) {
        Dialog(
            title = stringResource(Res.string.delete_chat),
            message = stringResource(Res.string.confirm_delete_chat_message),
            buttonText = stringResource(Res.string.delete),
            onActionClick = onConfirmDeleteChatClick,
            onDismiss = onDismissDeleteChatDialog
        )
    }

    dialog(showResendMessageDialog) {
        ResendMessageDialog(
            onDeleteMessageClick = onDeleteFailedMessageClick,
            onResendClick = onResendFailedMessageClick,
            onDismiss = onDismissResendMessageDialog
        )
    }
}
package net.thechance.mena.core_chat.presentation.screen.chat.components

import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope

fun ScaffoldScope.ChatScreenOverlays(
    showResendMessageDialog: Boolean,
    onDeleteFailedMessageClick: () -> Unit = { },
    onResendFailedMessageClick: () -> Unit = { },
    onDismissResendMessageDialog: () -> Unit = { },

    ) {

    dialog(showResendMessageDialog) {
        ResendMessageDialog(
            onDeleteMessageClick = onDeleteFailedMessageClick,
            onResendClick = onResendFailedMessageClick,
            onDismiss = onDismissResendMessageDialog
        )
    }
}
package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.actions
import mena.core_chat_presentation.generated.resources.delete_message
import mena.core_chat_presentation.generated.resources.ic_delete
import mena.core_chat_presentation.generated.resources.ic_refresh
import mena.core_chat_presentation.generated.resources.re_send
import net.thechance.mena.designsystem.presentation.component.dialog.BasicDialog
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun ScaffoldScope.resendFailedMessageDialog(
    showResendMessageDialog: Boolean,
    onDeleteFailedMessageClick: () -> Unit = { },
    onResendFailedMessageClick: () -> Unit = { },
    onDismissResendMessageDialog: () -> Unit = { },

    ) {

    dialog(showResendMessageDialog) {
        BasicDialog(
            isVisible = showResendMessageDialog,
            onDismiss = onDismissResendMessageDialog,
            onCancelClick = onDismissResendMessageDialog,
            actionButtons = {
                ActionMenuItem(
                    icon = painterResource(Res.drawable.ic_refresh),
                    text = stringResource(Res.string.re_send),
                    onClick = onResendFailedMessageClick
                )
                ActionMenuItem(
                    icon = painterResource(Res.drawable.ic_delete),
                    text = stringResource(Res.string.delete_message),
                    contentColor = Theme.colorScheme.error,
                    onClick = onDeleteFailedMessageClick
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
}

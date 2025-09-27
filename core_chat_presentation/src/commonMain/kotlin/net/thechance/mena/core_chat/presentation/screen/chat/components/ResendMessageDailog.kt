package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.actions
import mena.core_chat_presentation.generated.resources.delete_message
import mena.core_chat_presentation.generated.resources.ic_delete
import mena.core_chat_presentation.generated.resources.re_send
import mena.core_chat_presentation.generated.resources.ic_refresh
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ResendMessageDialog(
    onDeleteMessageClick: () -> Unit,
    onResendClick: () -> Unit,
    scrimColor: Color = Theme.colorScheme.primary.primary.copy(0.55f),
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scrimColor)
                .clickable(
                    onClick = onDismiss,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        )

        DialogContent(
            onDeleteMessageClick = onDeleteMessageClick,
            onResendClick = onResendClick,
            onCancelClick = onDismiss
        )

        BackHandler(enabled = true) {
            onDismiss()
        }
    }
}

@Composable
private fun DialogContent(
    onDeleteMessageClick: () -> Unit,
    onResendClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16)
            .background(
                Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.xl)
            )
    ) {
        // Cancel icon
        CancelIcon(
            onClick = onCancelClick,
            modifier = Modifier.align(Alignment.TopStart)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing._12, vertical = Theme.spacing._24)
        ) {
            Text(
                text = stringResource(Res.string.actions),
                style = Theme.typography.title.small,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Resend row
            ActionMenuItem(
                icon = painterResource(Res.drawable.ic_refresh),
                text = stringResource(Res.string.re_send),
                onClick = onResendClick
            )

            // Delete row
            ActionMenuItem(
                icon = painterResource(Res.drawable.ic_delete),
                text = stringResource(Res.string.delete_message),
                contentColor = Theme.colorScheme.error,
                onClick = onDeleteMessageClick
            )
        }
    }
}
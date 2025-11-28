package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.react_to_message
import net.thechance.mena.core_chat.presentation.screen.chat.MessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.TextMessageUiState
import net.thechance.mena.designsystem.presentation.component.dialog.BasicDialog
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private val availableReactions = listOf(
    "🔥",
    "❤️",
    "😂",
    "\uD83D\uDE22",
    "\uD83D\uDE20",
    "\uD83D\uDC4F\uD83C\uDFFB",
    "\uD83D\uDE4F\uD83C\uDFFB"
)

@OptIn(ExperimentalUuidApi::class)
fun ScaffoldScope.messageReactionDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit = { },
    message: MessageUiState? = null,
    currentUserId: Uuid? = null,
    onReactionClicked: (messageId: Uuid, emoji: String) -> Unit
) {
    if (message == null) return
    dialog(isVisible = isVisible) {

        BasicDialog(
            isVisible = isVisible,
            onDismiss = onDismiss,
            onCancelClick = onDismiss,
            contentPadding = PaddingValues(Theme.spacing._16),
            actionButtons = {
                MessageToReactDisplay(message = message)

                val selected =
                    message.messageDetails.reactions.firstOrNull { it.userId == currentUserId }?.emoji

                ReactionContent(
                    selectedReaction = selected,
                    onReactionSelected = { emoji ->
                        onReactionClicked(message.messageDetails.id, emoji)
                        onDismiss()
                    }
                )
            }
        ) {
            Text(
                text = stringResource(Res.string.react_to_message),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Theme.spacing._12)
            )
        }
    }
}

@Composable
fun MessageToReactDisplay(
    message: MessageUiState
) {
    if (message !is TextMessageUiState) {
        return
    }

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Theme.spacing._16)
            .background(
                color = if (message.messageDetails.isMine)
                    Theme.colorScheme.background.surface
                else
                    Theme.colorScheme.brand.brandVariant,
                shape = RoundedCornerShape(Theme.radius.md)
            )
    ) {
        Text(
            text = message.text,
            modifier = Modifier.padding(Theme.spacing._12),
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Start,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ReactionContent(
    onReactionSelected: (reaction: String) -> Unit,
    selectedReaction: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        availableReactions.forEach { reaction ->
            val isSelected = reaction == selectedReaction
            Box(
                modifier = Modifier.size(48.dp)
                    .clip(RoundedCornerShape(Theme.radius.full))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onReactionSelected(reaction) }
                    .background(
                        if (isSelected) Theme.colorScheme.brand.brandVariant
                        else Theme.colorScheme.background.surfaceLow,
                        shape = RoundedCornerShape(Theme.radius.full)
                    )
                    .padding(Theme.spacing._4),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = reaction,
                    style = Theme.typography.appName,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Preview
@Composable
private fun ReactionContentPreview() {
    ReactionContent(
        onReactionSelected = {}
    )
}
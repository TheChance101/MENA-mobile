package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import net.thechance.mena.core_chat.domain.entity.MessageReaction
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape

@Composable
fun ReactionBubble(
    reactions: List<MessageReaction>,
) {
    val grouped = reactions.groupBy { it.emoji }

    grouped.forEach { (emoji, list) ->
        val count = list.size
        val label = if (count > 1) "$count $emoji" else emoji

        Box(
            modifier = Modifier
                .offset(y = (-8).dp)
                .sizeIn(minWidth = 22.dp)
                .clip(CircleShape)
                .background(Theme.colorScheme.background.surface)
                .border(2.dp, Theme.colorScheme.background.surfaceLow,CircleShape)
                .padding(vertical = 4.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadeSecondary
            )
        }
    }
}

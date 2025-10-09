package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import net.thechance.mena.core_chat.domain.entity.MessageContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun MessageContent(
    messageContent: MessageContent,
    shape: Shape
) {
    when (messageContent) {
        is MessageContent.Text -> Text(
            text = messageContent.text,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )
        is MessageContent.ImageUrls -> ImageMessageContent(images = messageContent.imageUrls, modifier = Modifier.size(156.dp, 162.dp).clip(shape))
        is MessageContent.PendingImages -> ImageMessageContent(images = messageContent.images, modifier = Modifier.size(156.dp, 162.dp).clip(shape))
    }
}

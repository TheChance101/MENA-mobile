package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.runtime.Composable
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun MessageContent(
    messageContent: MessageContent
) {
    when (messageContent) {
        is MessageContent.Text -> Text(
            text = messageContent.text,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )
        is MessageContent.ImageUrls -> ImageMessageContent(images = messageContent.urls)
        is MessageContent.PendingImages -> ImageMessageContent(images = messageContent.byteArrays)
    }
}

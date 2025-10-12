package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import net.thechance.mena.core_chat.domain.entity.ImagesSource
import net.thechance.mena.core_chat.domain.entity.MessageContent
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

        is MessageContent.Images -> {
            val source = messageContent.source
            val images = when (source) {
                is ImagesSource.Local -> source.byteArrays
                is ImagesSource.Remote -> source.urls
            }
            ImageMessageContent(
                images = images,
                modifier = Modifier.size(156.dp, 162.dp).clip(shape)
            )
        }
    }
}

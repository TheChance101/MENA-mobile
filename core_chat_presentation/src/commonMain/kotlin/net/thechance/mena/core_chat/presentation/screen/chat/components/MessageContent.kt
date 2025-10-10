package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import net.thechance.mena.core_chat.presentation.screen.chat.MessageContentUiState
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun MessageContent(
    messageContentUiState: MessageContentUiState,
    shape: Shape,
    onImageClick: (Int) -> Unit = {}
) {
    when (messageContentUiState) {
        is MessageContentUiState.Text -> Text(
            text = messageContentUiState.text,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )

        is MessageContentUiState.ImageUrl -> ImageMessageContent(
            images = messageContentUiState.imageUrls,
            modifier = Modifier.size(156.dp, 162.dp).clip(shape),
            onImageClick = onImageClick
        )

        is MessageContentUiState.ImageByteArray -> ImageMessageContent(
            images = messageContentUiState.images,
            modifier = Modifier.size(156.dp, 162.dp).clip(shape)
        )
    }
}

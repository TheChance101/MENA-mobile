package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.preat.peekaboo.image.picker.toImageBitmap
import net.thechance.mena.core_chat.presentation.screen.chat.MessageUiState

@Composable
fun ImageMessageItem(
    message: MessageUiState,
    showMessageInfo: Boolean,
    isMarkedLastInSeries: Boolean,
    modifier: Modifier = Modifier,
    onFailClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    BaseMessageLayout(
        message = message,
        showMessageInfo = showMessageInfo,
        isMarkedLastInSeries = isMarkedLastInSeries,
        onFailClick = onFailClick,
        onMessageClick = onClick,
        modifier = modifier
    ) {
        message.imageBytes?.let { bytes ->
            Image(
                bitmap = bytes.toImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

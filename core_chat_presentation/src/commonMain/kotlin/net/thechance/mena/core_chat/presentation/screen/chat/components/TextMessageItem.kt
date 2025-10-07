@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.presentation.screen.chat.MessageStatusUiState
import net.thechance.mena.core_chat.presentation.screen.chat.TextMessageUiState
import net.thechance.mena.core_chat.presentation.utils.now
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun TextMessageItem(
    message: TextMessageUiState,
    chatAvatarUrl: String? = null,
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
        chatAvatarUrl = chatAvatarUrl,
        onFailClick = onFailClick,
        onMessageClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = message.text,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )
    }
}

@Composable
@Preview()
private fun PreviewTextMessageItem() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surface),
            contentAlignment = Alignment.BottomStart
        ) {
            TextMessageItem(
                modifier = Modifier,
                message = TextMessageUiState(
                    id = Uuid.random(),
                    sendTime = LocalDateTime.now(),
                    status = MessageStatusUiState.READ,
                    isMine = false,
                    text = "Hello Bilal"
                ),
                showMessageInfo = true,
                isMarkedLastInSeries = true
            )
        }
    }
}
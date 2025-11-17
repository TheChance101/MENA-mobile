package net.thechance.mena.core_chat.presentation.screen.home

import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.message_type_audio
import mena.core_chat_presentation.generated.resources.message_type_ayah
import mena.core_chat_presentation.generated.resources.message_type_photo
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status.Read
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status.Received
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status.Sent
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status.UnRead
import net.thechance.mena.core_chat.presentation.utils.UiText
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun ChatSummary.toUi(): ChatUiState {

    val statusMessages = getStatusMessages(lastMessage, unReadMessagesCount)
    val lastMessage = lastMessage?.let {
        ChatUiState.MessageUiState(
            text = it.content.toPreviewText(),
            isMine = it.isMine,
            time = it.sendAt,
        )
    }
    return ChatUiState(
        id = id,
        name = name,
        imageUrl = imageUrl,
        lastMessage = lastMessage,
        status = statusMessages
    )
}


fun MessageContent.toPreviewText(): UiText {
    return when (this) {
        is MessageContent.Text -> UiText.DynamicString(text)
        is MessageContent.Audio -> UiText.StringRes(Res.string.message_type_audio)
        is MessageContent.Image -> UiText.StringRes(Res.string.message_type_photo)
        is MessageContent.Ayah -> UiText.StringRes(Res.string.message_type_ayah)
    }
}

private fun getStatusMessages(lastMessage: Message?, unReadMessagesCount: Int): Status {
    if (lastMessage == null) return Received
    return when {
        lastMessage.isMine -> {
            if (unReadMessagesCount == 0) {
                Read
            } else {
                Sent
            }
        }

        else -> {
            if (unReadMessagesCount > 0) {
                UnRead(unReadMessagesCount)
            } else {
                Received
            }
        }
    }
}
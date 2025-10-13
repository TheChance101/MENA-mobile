package net.thechance.mena.core_chat.presentation.screen.home

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status
import net.thechance.mena.core_chat.presentation.utils.format
import net.thechance.mena.core_chat.presentation.utils.formatAsTime
import net.thechance.mena.core_chat.presentation.utils.minusDays
import net.thechance.mena.core_chat.presentation.utils.now
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun ChatSummary.toUi(): ChatUiState {

    val statusMessages = getStatusMessages(this)
    val formattedTime = getFormattedTime(lastMessage.sendAt)

    return ChatUiState(
        id = id,
        name = name,
        imageUrl = imageUrl,
        lastMessage = ChatUiState.MessageUiState(
            text = lastMessage.content,
            time = formattedTime,
            isMine = lastMessage.isMine
        ),
        status = statusMessages
    )
}

private fun getStatusMessages(chatSummary: ChatSummary): Status = when {
    !chatSummary.lastMessage.isMine && chatSummary.unReadMessagesCount > 0 ->
        Status.UnRead(chatSummary.unReadMessagesCount)

    chatSummary.lastMessage.isMine && chatSummary.unReadMessagesCount > 0 ->
        Status.Sent

    chatSummary.lastMessage.isMine && chatSummary.unReadMessagesCount == 0 ->
        Status.Read

    else ->
        Status.Sent
}

private fun getFormattedTime(messageDateTime: LocalDateTime): String {
    val now = LocalDateTime.now()
    val today = now.date
    val messageDate = messageDateTime.date

    return when (messageDate) {
        today -> messageDateTime.formatAsTime()
        today.minusDays(1) -> "Yesterday"
        else -> messageDate.format("dd-MM-yyyy")
    }
}
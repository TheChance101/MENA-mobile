package net.thechance.mena.core_chat.presentation.screen.home

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.entity.ChatSummaryStatus
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.HomeUiState
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.HomeUiState.Status
import net.thechance.mena.core_chat.presentation.utils.format
import net.thechance.mena.core_chat.presentation.utils.formatAsTime
import net.thechance.mena.core_chat.presentation.utils.minusDays
import net.thechance.mena.core_chat.presentation.utils.now
import net.thechance.mena.core_chat.presentation.utils.parseToLocalDateTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun ChatSummary.toUi(): HomeUiState {
    val statusMessages = getStatusMessages(status)

    val messageDateTime = parseToLocalDateTime(lastMessageTime)
    val formattedTime = getFormattedTime(messageDateTime)

    return HomeUiState(
        id = id,
        name = name,
        imageUrl = imageUrl,
        lastMessage = lastMessage,
        time = formattedTime,
        isMine = status.isMine,
        status = statusMessages
    )
}

private fun getStatusMessages(status: ChatSummaryStatus): Status = when {
    !status.isMine && status.unReadMessagesCount > 0 ->
        Status.UnRead(status.unReadMessagesCount)

    !status.isMine ->
        Status.Received

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
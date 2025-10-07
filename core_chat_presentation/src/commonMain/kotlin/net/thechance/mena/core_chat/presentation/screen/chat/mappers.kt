@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.today
import mena.core_chat_presentation.generated.resources.yesterday
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.format
import net.thechance.mena.core_chat.presentation.utils.minusDays
import net.thechance.mena.core_chat.presentation.utils.now
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import net.thechance.mena.core_chat.domain.entity.MessageStatus as DomainMessageStatus


fun Message.toUi(currentUserId: Uuid): TextMessageUiState {
    return TextMessageUiState(
        id = id,
        senderId = senderId,
        chatId = chatId,
        sendTime = sendAt,
        status = status.toUi(),
        isMine = senderId == currentUserId,
        text = text
    )
}

fun TextMessageUiState.toEntity(): Message {
    return Message(
        id = id,
        senderId = senderId,
        chatId = chatId,
        text = text,
        sendAt = sendTime,
        status = status.toEntity()
    )
}


private fun DomainMessageStatus.toUi(): MessageStatusUiState {
    return when (this) {
        DomainMessageStatus.LOADING -> MessageStatusUiState.SENDING
        DomainMessageStatus.SENT -> MessageStatusUiState.SENT
        DomainMessageStatus.READ -> MessageStatusUiState.READ
        DomainMessageStatus.FAILED -> MessageStatusUiState.FAILED
    }
}

private fun MessageStatusUiState.toEntity(): DomainMessageStatus {
    return when (this) {
        MessageStatusUiState.SENDING -> DomainMessageStatus.LOADING
        MessageStatusUiState.SENT -> DomainMessageStatus.SENT
        MessageStatusUiState.READ -> DomainMessageStatus.READ
        MessageStatusUiState.FAILED -> DomainMessageStatus.FAILED
    }
}


fun List<TextMessageUiState>.markLastInSeries(): List<MarkedMessageUiState> {
    return this.mapIndexed { index, message ->
        val nextIsMine = this.getOrNull(index - 1)?.isMine
        val isLastInSeries = nextIsMine != message.isMine

        MarkedMessageUiState(message, isLastInSeries)
    }
}

fun List<MarkedMessageUiState>.withDateSeparators(): List<ChatListItem> {
    if (isEmpty()) return emptyList()

    val today = LocalDateTime.now().date
    val yesterday = today.minusDays(1)

    return asReversed()
        .groupBy { it.message.sendTime.date }
        .flatMap { (date, messages) ->
            val markedMessages = messages.markLastInGroup()

            buildList {
                add(ChatListItem.DateSeparator(date.toLabel(today, yesterday)))
                addAll(markedMessages.map { ChatListItem.Message(it) })
            }
        }
        .asReversed()
}

private fun List<MarkedMessageUiState>.markLastInGroup(): List<MarkedMessageUiState> {
    return mapIndexed { index, message ->
        if (index == lastIndex)
            message.copy(isMarkedLastInSeries = true)
        else
            message
    }
}

private fun LocalDate.toLabel(
    today: LocalDate,
    yesterday: LocalDate,
    todayLabel: UiText = UiText.StringRes(Res.string.today),
    yesterdayLabel: UiText = UiText.StringRes(Res.string.yesterday)
): UiText = when (this) {
    today -> todayLabel
    yesterday -> yesterdayLabel
    else -> UiText.DynamicString(format())
}



fun List<ChatListItem>.toggleMessageInfo(messageId: Uuid): List<ChatListItem> = map { item ->
    if (item is ChatListItem.Message && item.data.message.id == messageId)
        item.copy(data = item.data.copy(showMessageInfo = !item.data.showMessageInfo))
    else item
}


fun List<TextMessageUiState>.buildListItems(): List<ChatListItem> {
    return sortedByDescending { it.sendTime }.markLastInSeries().withDateSeparators()
}
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


fun Message.toUi(currentUserId: Uuid): MessageUiState {
    return MessageUiState(
        id = id,
        senderId = senderId,
        chatId = chatId,
        sendTime = sendAt,
        status = status,
        isMine = senderId == currentUserId,
        content = content
    )
}

fun MessageUiState.toEntity(): Message {
    return Message(
        id = id,
        senderId = senderId,
        chatId = chatId,
        content = content,
        sendAt = sendTime,
        status = status,
        isMine = isMine
    )
}

fun List<MessageUiState>.markLastInSeries(): List<MessageUiState> {
    return this.mapIndexed { index, message ->
        val nextIsMine = this.getOrNull(index - 1)?.isMine
        val isLastInSeries = nextIsMine != message.isMine

        message.copy(isLastInSeries = isLastInSeries)
    }
}

fun List<MessageUiState>.withDateSeparators(): List<ChatListItem> {
    if (isEmpty()) return emptyList()

    val today = LocalDateTime.now().date
    val yesterday = today.minusDays(1)

    return asReversed()
        .groupBy { it.sendTime.date }
        .flatMap { (date, messages) ->
            val markedMessages = messages.markLastInGroup()

            buildList {
                add(ChatListItem.DateSeparator(date.toLabel(today, yesterday)))
                addAll(markedMessages.map { ChatListItem.Message(it) })
            }
        }
        .asReversed()
}

private fun List<MessageUiState>.markLastInGroup(): List<MessageUiState> {
    return mapIndexed { index, message ->
        if (index == lastIndex)
            message.copy(isLastInSeries = true)
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
    if (item is ChatListItem.Message && item.data.id == messageId)
        item.copy(data = item.data.copy(isVisibleMessageInfo = !item.data.isVisibleMessageInfo))
    else item
}


fun List<MessageUiState>.buildListItems(): List<ChatListItem> {
    return sortedByDescending { it.sendTime }.markLastInSeries().withDateSeparators()
}
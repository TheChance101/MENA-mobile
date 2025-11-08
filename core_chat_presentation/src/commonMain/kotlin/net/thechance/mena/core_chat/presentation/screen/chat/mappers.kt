@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.today
import mena.core_chat_presentation.generated.resources.yesterday
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.format
import net.thechance.mena.core_chat.presentation.utils.minusDays
import net.thechance.mena.core_chat.presentation.utils.now
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Message.toUi(): MessageUiState {
    return MessageUiState(
        id = id,
        senderId = senderId,
        chatId = chatId,
        sendTime = sendAt,
        status = status,
        isMine = isMine,
        content = content,
        reactions = reactions
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
        isMine = isMine,
        reactions = reactions
    )
}

fun List<MessageUiState>.markLastInSeries(): List<MessageUiState> {
    return this.mapIndexed { index, message ->
        val nextIsMine = this.getOrNull(index - 1)?.isMine
        val nextStatus = this.getOrNull(index - 1)?.status
        val isLastInSeries = nextIsMine != message.isMine || nextStatus != message.status
        message.copy(isLastInSeries = isLastInSeries)
    }
}

fun List<MessageUiState>.toChatList(shouldGroupMessages: (MessageUiState) -> Boolean): List<ChatListItem> {
    if (isEmpty()) return emptyList()

    val today = LocalDateTime.now().date
    val yesterday = today.minusDays(1)

    return asReversed()
        .groupBy { it.sendTime.date }
        .flatMap { (date, messages) ->
            val markedMessages = messages.markLastInGroup()
            val groupedMessages = markedMessages.toGroupedMessagesChatList(shouldGroupMessages)

            buildList {
                add(ChatListItem.DateSeparator(date.toLabel(today, yesterday)))
                addAll(groupedMessages)
            }
        }
        .asReversed()
}


fun List<MessageUiState>.toGroupedMessagesChatList(shouldGroupMessages: (MessageUiState) -> Boolean): List<ChatListItem> {
    val grouped = mutableListOf<ChatListItem>()
    var tempImages = mutableListOf<MessageUiState>()

    fun groupAndClear() {
        if (tempImages.isNotEmpty()) {
            grouped.add(ChatListItem.ImageMessages(tempImages.toList()))
            tempImages = mutableListOf()
        }
    }

    for (msg in this) {
        when (msg.content) {
            is MessageContent.Image -> {
                val last = tempImages.lastOrNull()
                if (last != null && last.isMine == msg.isMine && last.status == msg.status && shouldGroupMessages(
                        msg
                    )
                ) {
                    tempImages.add(msg)
                } else {
                    groupAndClear()
                    tempImages.add(msg)
                }
            }
            is MessageContent.Audio -> {
                groupAndClear()
                grouped.add(ChatListItem.VoiceMessage(
                    data = msg,
                    isPlaying = false,
                    isLoading = false,
                    progress = 0f,
                    duration = msg.content.audioDurationMs ?: 0L,
                    waveformData = generateWaveformData()
                ))
            }
            is MessageContent.Text -> {
                groupAndClear()
                grouped.add(ChatListItem.TextMessage(msg))
            }
        }
    }

    groupAndClear()
    return grouped
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

fun generateWaveformData(): List<Float> {
    return List(50) { kotlin.random.Random.nextFloat() * 0.8f + 0.2f }
}

fun List<ChatListItem>.toggleMessageInfo(messageId: Uuid): List<ChatListItem> = map { item ->
    if (item is ChatListItem.TextMessage && item.data.id == messageId)
        item.copy(data = item.data.copy(isVisibleMessageInfo = !item.data.isVisibleMessageInfo))
    else if (item is ChatListItem.VoiceMessage && item.data.id == messageId)
        item.copy(data = item.data.copy(isVisibleMessageInfo = !item.data.isVisibleMessageInfo))
    else item
}


fun List<MessageUiState>.buildListItems(shouldGroupImageMessages: (MessageUiState) -> Boolean): List<ChatListItem> {
    return sortedByDescending { it.sendTime }.markLastInSeries()
        .toChatList(shouldGroupImageMessages)
}
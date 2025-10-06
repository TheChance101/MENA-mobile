@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.error
import mena.core_chat_presentation.generated.resources.today
import mena.core_chat_presentation.generated.resources.yesterday
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.format
import net.thechance.mena.core_chat.presentation.utils.minusDays
import net.thechance.mena.core_chat.presentation.utils.now
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


fun List<TextMessageUiState>.markLastInSeries(): List<MarkedMessageUiState> {
    return this.mapIndexed { index, message ->
        val nextIsMine = this.getOrNull(index - 1)?.isMine
        val isLastInSeries = nextIsMine != message.isMine

        MarkedMessageUiState(message, isLastInSeries)
    }
}

fun List<MarkedMessageUiState>.withDateSeparators(
    todayLabel: UiText = UiText.StringRes(Res.string.today),
    yesterdayLabel: UiText = UiText.StringRes(Res.string.yesterday)
): List<ChatListItem> {
    if (this.isEmpty()) return emptyList()

    val today = LocalDateTime.now().date
    val yesterday = today.minusDays(1)

    val result = mutableListOf<ChatListItem>()
    var lastDate: LocalDate? = null
    var lastMessageIndexInDate: Int? = null

    val reversed = this.asReversed()

    for ((index, item) in reversed.withIndex()) {
        val messageDate = item.message.sendTime.date

        if (messageDate != lastDate) {
            lastMessageIndexInDate?.let { idx ->
                val msgItem = result[idx] as ChatListItem.Message
                result[idx] = msgItem.copy(
                    data = msgItem.data.copy(isMarkedLastInSeries = true)
                )
            }

            val label = when (messageDate) {
                today -> todayLabel
                yesterday -> yesterdayLabel
                else -> UiText.DynamicString(messageDate.format())
            }
            result.add(ChatListItem.DateSeparator(label))
            lastDate = messageDate
        }

        result.add(ChatListItem.Message(item))
        lastMessageIndexInDate = result.lastIndex

        if (index == reversed.lastIndex) {
            val msgItem = result[lastMessageIndexInDate] as ChatListItem.Message
            result[lastMessageIndexInDate] = msgItem.copy(
                data = msgItem.data.copy(isMarkedLastInSeries = true)
            )
        }
    }

    return result.asReversed()
}


fun List<ChatListItem>.toggleMessageInfo(messageId: Uuid): List<ChatListItem> = map { item ->
    if (item is ChatListItem.Message && item.data.message.id == messageId)
        item.copy(data = item.data.copy(showMessageInfo = !item.data.showMessageInfo))
    else item
}


fun List<TextMessageUiState>.buildListItems(): List<ChatListItem> {
    return sortedByDescending { it.sendTime }.markLastInSeries().withDateSeparators()
}
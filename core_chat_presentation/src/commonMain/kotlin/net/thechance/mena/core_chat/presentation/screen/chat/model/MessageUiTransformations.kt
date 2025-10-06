@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import mena.core_chat_presentation.generated.resources.Res
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

fun List<MarkedMessageUiState>.withDateSeparators(): List<ChatListItem> {
    if (isEmpty()) return emptyList()

    val today = LocalDateTime.now().date
    val yesterday = today.minusDays(1)

    return asReversed()
        .groupBy { it.message.sendTime.date }
        .flatMap { (date, messages) ->
            buildList {
                add(ChatListItem.DateSeparator(date.toLabel(today, yesterday)))
                addAll(messages.map { ChatListItem.Message(it) })
            }
        }
        .asReversed()
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
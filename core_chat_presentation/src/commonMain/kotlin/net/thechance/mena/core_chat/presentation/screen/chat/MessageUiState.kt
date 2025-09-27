package net.thechance.mena.core_chat.presentation.screen.chat

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.presentation.utils.format
import net.thechance.mena.core_chat.presentation.utils.minusDays
import net.thechance.mena.core_chat.presentation.utils.now

abstract class MessageUiState(
    open val id: String,
    open val senderId: String,
    open val sendTime: LocalDateTime,
    open val status: MessageStatus,
    open val isMine: Boolean
)

enum class MessageStatus {
    SENDING,
    SENT,
    READ,
    FAILED
}

data class MarkedMessageUiState(
    val message: MessageUiState,
    val isMarkedLastInSeries: Boolean,
    val showMessageInfo: Boolean = false
)

data class TextMessageUiState(
    override val id: String,
    override val senderId: String,
    override val sendTime: LocalDateTime,
    override val status: MessageStatus,
    override val isMine: Boolean,
    val text: String
): MessageUiState(
    id,
    senderId,
    sendTime,
    status,
    isMine
)

fun List<MessageUiState>.markLastInSeries(): List<MarkedMessageUiState> {
    return this.mapIndexed { index, message ->
        val nextSender = this.getOrNull(index - 1)?.senderId
        val isLastInSeries = nextSender == null || nextSender != message.senderId

        MarkedMessageUiState(message, isLastInSeries)
    }
}

fun List<MarkedMessageUiState>.withDateSeparators(
    todayLabel: String = "Today",
    yesterdayLabel: String = "Yesterday"
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
                else -> messageDate.format()
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


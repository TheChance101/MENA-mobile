package net.thechance.mena.core_chat.domain.entity

data class ChatSummaryStatus(
    val isMine: Boolean,
    val unReadMessagesCount: Int
)
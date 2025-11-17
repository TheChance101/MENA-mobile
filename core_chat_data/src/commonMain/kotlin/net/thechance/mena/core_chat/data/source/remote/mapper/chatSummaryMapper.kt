@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.source.remote.mapper

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.utils.toUuid
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.model.PagedData
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun PagedDataDto<ChatSummaryDto>.toPagedListOfChatSummary(): PagedData<ChatSummary> {
    val pagedData = this
    return PagedData(
        data = pagedData.data
            .toListOfChatSummary()
            .filter { chatSummary -> chatSummary.lastMessage != null },
        totalItems = pagedData.totalItems,
        isLastPage = pagedData.pageNumber >= pagedData.totalPages
    )
}

private fun List<ChatSummaryDto>.toListOfChatSummary(): List<ChatSummary> {
    return mapNotNull { it.toDomain() }
}

@OptIn(ExperimentalTime::class)
fun ChatSummaryDto.toDomain(): ChatSummary {
    val lastMessage = lastMessage?.let { lastMsg ->
        Message(
            id = Uuid.random(),
            senderId = Uuid.random(),
            chatId = id.toUuid(),
            sendAt = Instant.parse(lastMsg.sentAt).toLocalDateTime(TimeZone.currentSystemDefault()),
            status = MessageStatus.SENT,
            content = MessageContent.Text(lastMsg.content),
            isMine = lastMsg.isMine
        )
    }

    return ChatSummary(
        id = id.toUuid(),
        name = name,
        imageUrl = imageUrl.orEmpty(),
        lastMessage = lastMessage,
        unReadMessagesCount = unReadMessagesCount
    )
}
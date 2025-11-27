@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.source.remote.mapper

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageContentDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.utils.toUuid
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.entity.LastMessageType
import net.thechance.mena.core_chat.domain.model.PagedData
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

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

private fun MessageContentDto.toLastMessageType(): LastMessageType = when (this) {
    is MessageContentDto.Text -> LastMessageType.Text(text)
    is MessageContentDto.Image -> LastMessageType.Image
    is MessageContentDto.Audio -> LastMessageType.Audio
    is MessageContentDto.Money -> LastMessageType.Money
    is MessageContentDto.Ayah -> LastMessageType.Ayah
    is MessageContentDto.Order -> LastMessageType.Order
}

@OptIn(ExperimentalTime::class)
fun ChatSummaryDto.toDomain(): ChatSummary {
    val lastMessage = lastMessage?.let {
        ChatSummary.Message(
            type = it.content.toLastMessageType(),
            sendAt = Instant.parse(it.sendAt).toLocalDateTime(TimeZone.currentSystemDefault()),
            isMine = it.isMine
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
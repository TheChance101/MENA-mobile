@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.source.remote.mapper

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.utils.getUuidOrNull
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.model.PagedData
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

fun PagedDataDto<ChatSummaryDto>.toPagedListOfChatSummary(): PagedData<ChatSummary> {
    val pagedData = this
    return PagedData(
        data = pagedData.data.orEmpty().toListOfChatSummary(),
        totalItems = pagedData.totalItems ?: 0,
        isLastPage = (pagedData.pageNumber ?: 0) >= (pagedData.totalPages ?: 0)
    )
}

private fun List<ChatSummaryDto>.toListOfChatSummary(): List<ChatSummary> {
    return mapNotNull { it.toDomain() }
}

@OptIn(ExperimentalTime::class)
fun ChatSummaryDto.toDomain(): ChatSummary? {
    val sendAt = Instant.parse(lastMessage.sentAt).toLocalDateTime(TimeZone.currentSystemDefault())

    return ChatSummary(
        id = getUuidOrNull(id) ?: return null,
        name = name,
        imageUrl = imageUrl.orEmpty(),
        lastMessage = ChatSummary.Message(
            content = lastMessage.content,
            sendAt = sendAt,
            isMine = lastMessage.isMine == true
        ),
        unReadMessagesCount = unReadMessagesCount
    )
}
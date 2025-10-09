@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.source.remote.mapper

import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryStatusDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.utils.getUuidOrNull
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.entity.ChatSummaryStatus
import net.thechance.mena.core_chat.domain.exception.ContactsFetchFailedException
import net.thechance.mena.core_chat.domain.model.PagedData
import kotlin.collections.orEmpty
import kotlin.uuid.ExperimentalUuidApi

fun PagedDataDto<ChatSummaryDto>?.toPagedListOfChatSummary(): PagedData<ChatSummary> {
    val pagedData = this ?: throw ContactsFetchFailedException("Response body is null")
    return PagedData(
        data = pagedData.data.orEmpty().toListOfChatSummary(),
        totalItems = pagedData.totalItems ?: 0,
        isLastPage = (pagedData.pageNumber ?: 0) >= (pagedData.totalPages ?: 0)
    )
}

private fun List<ChatSummaryDto>.toListOfChatSummary(): List<ChatSummary> {
    return mapNotNull { it.toDomain() }
}

fun ChatSummaryDto.toDomain(): ChatSummary? {
    return ChatSummary(
        id = getUuidOrNull(id) ?: return null,
        imageUrl = imageUrl.orEmpty(),
        lastMessage = lastMessage.orEmpty(),
        lastMessageTime = lastMessageTime.orEmpty(),
        name = name.orEmpty(),
        status = chatSummaryStatusDto.toDomain()
    )
}

fun ChatSummaryStatusDto?.toDomain(): ChatSummaryStatus =
    ChatSummaryStatus(this?.isMine ?: false, this?.unReadMessagesCount ?: 0)

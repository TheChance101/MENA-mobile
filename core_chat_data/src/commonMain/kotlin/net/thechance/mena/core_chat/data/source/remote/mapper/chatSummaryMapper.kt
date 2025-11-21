@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.source.remote.mapper

import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.utils.toUuid
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.model.PagedData
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import net.thechance.mena.faith.domain.service.QuranService

suspend fun PagedDataDto<ChatSummaryDto>.toPagedListOfChatSummary(quranService: QuranService): PagedData<ChatSummary> {
    val pagedData = this
    return PagedData(
        data = pagedData.data
            .toListOfChatSummary(quranService)
            .filter { chatSummary -> chatSummary.lastMessage != null },
        totalItems = pagedData.totalItems,
        isLastPage = pagedData.pageNumber >= pagedData.totalPages
    )
}

private suspend fun List<ChatSummaryDto>.toListOfChatSummary(quranService: QuranService): List<ChatSummary> {
    return mapNotNull { it.toDomain(quranService) }
}

@OptIn(ExperimentalTime::class)
suspend fun ChatSummaryDto.toDomain(quranService: QuranService): ChatSummary {
    val lastMessage = lastMessage?.toDomain(quranService)

    return ChatSummary(
        id = id.toUuid(),
        name = name,
        imageUrl = imageUrl.orEmpty(),
        lastMessage = lastMessage,
        unReadMessagesCount = unReadMessagesCount
    )
}
package net.thechance.mena.core_chat.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.util.reflect.typeInfo
import net.thechance.mena.core_chat.data.source.remote.dto.ChatDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.source.remote.mapper.toPagedListOfChatSummary
import net.thechance.mena.core_chat.data.source.remote.network.ImageDownloader
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.data.source.remote.network.tryNetworkCall
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import net.thechance.mena.core_chat.domain.exception.OperationFailedException
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ChatRepositoryImpl(
    private val client: HttpClient,
    private val webSocketManager: WebSocketManager,
    private val imageDownloader: ImageDownloader,
) : ChatRepository{

    override suspend fun getChatsSummary(pageNumber: Int, pageSize: Int): PagedData<ChatSummary> {
        return tryNetworkCall<PagedDataDto<ChatSummaryDto>>(
            bodyType = typeInfo<PagedDataDto<ChatSummaryDto>>()
        ) {
            client.get(CHAT_SUMMARY_ENDPOINT) {
                parameter(PAGE_NUMBER_PARAMETER, pageNumber)
                parameter(PAGE_SIZE_PARAMETER, pageSize)
            }
        }?.toPagedListOfChatSummary() ?: throw NotFoundException("Response body is null")
    }

    override suspend fun getChatSummaryById(chatId: Uuid): ChatSummary {
        return tryNetworkCall<ChatSummaryDto>(
            bodyType = typeInfo<ChatSummaryDto>()
        ) {
            client.get("$CHAT_SUMMARY_ENDPOINT/$chatId")
        }?.toDomain() ?: throw NotFoundException("Chat not found")
    }


    override suspend fun getChatByContactUserId(userId: Uuid): Chat {
        return tryNetworkCall<ChatDto>(
            bodyType = typeInfo<ChatDto>()
        ) {
            client.get(CHAT_ENDPOINT) {
                parameter(RECEIVER_ID_PARAMETER, userId)
            }
        }?.toDomain() ?: throw NotFoundException("Chat not found")
    }

    override suspend fun getChatById(chatId: Uuid): Chat {
        return tryNetworkCall<ChatDto>(bodyType = typeInfo<ChatDto>()) {
            client.get("$CHAT_ENDPOINT/$chatId")
        }?.toDomain() ?: throw NotFoundException("Chat not found")
    }


    override suspend fun downloadImage(url: String) {
        val success = imageDownloader.downloadImageToGallery(url)
        if (!success) {
            throw OperationFailedException("Failed to download image")
        }
    }

    override suspend fun disconnect() {
        webSocketManager.disconnect()
    }

    private companion object {
        const val PAGE_NUMBER_PARAMETER = "page"
        const val PAGE_SIZE_PARAMETER = "size"
        const val RECEIVER_ID_PARAMETER = "receiverId"
        const val CHAT_ENDPOINT = "/chat"
        const val CHAT_SUMMARY_ENDPOINT = "/chat/chatsSummary"
    }
}
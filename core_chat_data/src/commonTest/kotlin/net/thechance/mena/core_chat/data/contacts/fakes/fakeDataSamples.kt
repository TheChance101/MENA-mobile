@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.contacts.fakes

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary.CachedChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatDto
import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import net.thechance.mena.core_chat.data.source.remote.dto.ContactDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageReactionDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageContentDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.utils.now
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import com.bilalazzam.contacts_provider.Contact as DeviceContact

val sampleDeviceContact = createDeviceContact(
    id = "1",
    firstName = "Bilal",
    lastName = "Azzam",
    phoneNumbers = listOf("01026388780")
)

val sampleContactDto = createContactDto(
    firstName = "Bilal",
    lastName = "Azzam",
    phone = "01026388780",
    menaUserId = "9a629aaa-8907-4dc6-ac33-f79fef7b4251",
    imageUrl = "http://example.com/image.jpg"
)

val sampleContact = sampleContactDto.toDomain()

fun createPagedDataDto(
    data: List<ContactDto> = emptyList(),
    pageNumber: Int = 1,
    pageSize: Int = 10,
    totalItems: Int = 15,
    totalPages: Int = 2
) = PagedDataDto(
    data = data,
    pageNumber = pageNumber,
    pageSize = pageSize,
    totalItems = totalItems,
    totalPages = totalPages
)

fun createContactDto(
    firstName: String? = null,
    lastName: String? = null,
    phone: String? = null,
    menaUserId: String? = null,
    imageUrl: String? = null
) = ContactDto(firstName, lastName, phone, menaUserId, imageUrl)

fun createDeviceContact(
    id: String = "1",
    firstName: String? = "A",
    lastName: String? = "B",
    phoneNumbers: List<String> = emptyList()
) = DeviceContact(id, firstName, lastName, phoneNumbers)

fun createMessage(
    id: Uuid = Uuid.random(),
    senderId: Uuid = Uuid.random(),
    chatId: Uuid = Uuid.random(),
    content: MessageContent = MessageContent.Text("Hello from test"),
    sendAt: LocalDateTime = LocalDateTime.now(),
    status: MessageStatus = MessageStatus.SENT,
    isMine: Boolean = true
) = Message(
    id = id,
    senderId = senderId,
    chatId = chatId,
    content = content,
    sendAt = sendAt,
    status = status,
    isMine = isMine
)

fun createMessageDto(
    id: String = Uuid.random().toString(),
    senderId: String = Uuid.random().toString(),
    chatId: String = Uuid.random().toString(),
    content: MessageContentDto = MessageContentDto.Text("Hello from history"),
    reactions: List<MessageReactionDto> = emptyList(),
    sendAt: String = "2025-10-01T12:00:00Z",
    isRead: Boolean = false,
    isMine: Boolean = true
) = MessageDto(
    id = id,
    senderId = senderId,
    chatId = chatId,
    content = content,
    reactions = reactions,
    sendAt = sendAt,
    isRead = isRead,
    isMine = isMine
)
    fun createChatDto(
    id: String = Uuid.random().toString(),
    name: String = "Test Chat",
    imageUrl: String? = null,
    requesterId: String = Uuid.random().toString(),
    receiverId: String = Uuid.random().toString()
) = ChatDto(
    id = id,
    name = name,
    imageUrl = imageUrl,
    requesterId = requesterId,
    receiverId = receiverId
)

fun createLastMessageDto(
    content: String = "Hello there",
    sentAt: String = "2025-10-01T12:00:00Z",
    isMine: Boolean = false
) = ChatSummaryDto.LastMessageDto(
    content = content,
    sentAt = sentAt,
    isMine = isMine
)

fun createChatSummaryDto(
    id: String = Uuid.random().toString(),
    imageUrl: String? = "http://example.com/image.jpg",
    name: String = "Test Chat",
    lastMessage: ChatSummaryDto.LastMessageDto = createLastMessageDto(),
    unReadMessagesCount: Int = 1
) = ChatSummaryDto(
    id = id,
    imageUrl = imageUrl,
    lastMessage = lastMessage,
    name = name,
    unReadMessagesCount = unReadMessagesCount
)

fun createCachedChatSummaryDto(
    id: String = Uuid.random().toString(),
    imageUrl: String? = "http://example.com/image.jpg",
    name: String = "Test Chat",
    unReadMessagesCount: Int = 1
) = CachedChatSummaryDto(
    id = id,
    imageUrl = imageUrl ?: "",
    name = name,
    unReadMessagesCount = unReadMessagesCount,
    lastMessageContent = "hello",
    lastMessageSentAt = "2025-10-01T12:00:00",
    lastMessageIsMine = false
)
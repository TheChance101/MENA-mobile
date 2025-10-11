@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.contacts.fakes

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.data.source.remote.dto.ChatDto
import net.thechance.mena.core_chat.data.source.remote.dto.ContactDto
import net.thechance.mena.core_chat.data.source.remote.dto.MessageDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.utils.now
import net.thechance.mena.core_chat.domain.entity.Message
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
    data: List<ContactDto>? = null,
    pageNumber: Int? = 1,
    pageSize: Int? = 10,
    totalItems: Int? = 15,
    totalPages: Int? = 2
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
    text: String = "fail",
    sendAt: LocalDateTime = LocalDateTime.now(),
    status: MessageStatus = MessageStatus.SENT
) = Message(
    id = id,
    senderId = senderId,
    chatId = chatId,
    text = text,
    sendAt = sendAt,
    status = status
)

fun createMessageDto(
    id: String = Uuid.random().toString(),
    senderId: String = Uuid.random().toString(),
    chatId: String = Uuid.random().toString(),
    text: String = "Hello from history",
    sendAt: String = "2025-10-01T12:00:00Z",
    isRead: Boolean = false
) = MessageDto(
    id = id,
    senderId = senderId,
    chatId = chatId,
    text = text,
    sendAt = sendAt,
    isRead = isRead
)

fun createChatDto(
    id: String = Uuid.random().toString(),
    name: String = "Test Chat",
    imageUrl: String? = null,
    requesterId: String = Uuid.random().toString()
) = ChatDto(
    id = id,
    name = name,
    imageUrl = imageUrl,
    requesterId = requesterId
)
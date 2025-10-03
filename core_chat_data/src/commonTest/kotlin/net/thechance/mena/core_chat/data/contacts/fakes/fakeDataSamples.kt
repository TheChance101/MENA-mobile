@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.contacts.fakes

import net.thechance.mena.core_chat.data.contacts.dto.ContactDto
import net.thechance.mena.core_chat.data.contacts.toDomain
import net.thechance.mena.core_chat.data.shared.dto.PagedDataDto
import kotlin.uuid.ExperimentalUuidApi
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



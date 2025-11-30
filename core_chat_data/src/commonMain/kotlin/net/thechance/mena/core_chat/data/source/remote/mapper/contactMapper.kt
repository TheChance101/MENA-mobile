package net.thechance.mena.core_chat.data.source.remote.mapper

import net.thechance.mena.core_chat.data.source.remote.dto.ContactCreationRequestDto
import net.thechance.mena.core_chat.data.source.remote.dto.ContactDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.utils.getUuidOrNull
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.exception.ContactsFetchFailedException
import net.thechance.mena.core_chat.domain.model.PagedData
import kotlin.uuid.ExperimentalUuidApi
import com.bilalazzam.contacts_provider.Contact as DeviceContact

@OptIn(ExperimentalUuidApi::class)
fun PagedDataDto<ContactDto>?.toPagedListOfContacts(): PagedData<Contact> {
    val pagedData = this ?: throw ContactsFetchFailedException("Response body is null")
    return PagedData(
        data = pagedData.data.toListOfContact(),
        totalItems = pagedData.totalItems,
        isLastPage = pagedData.pageNumber >= pagedData.totalPages
    )
}

@OptIn(ExperimentalUuidApi::class)
private fun List<ContactDto>.toListOfContact(): List<Contact> {
    return map { it.toDomain() }
}

@OptIn(ExperimentalUuidApi::class)
fun ContactDto.toDomain(): Contact {
    return Contact(
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        phone = phoneNumber.orEmpty(),
        menaUserId = getUuidOrNull(menaUserId),
        imageUrl = imageUrl?.let { BASE_CONTACT_IMAGE_URL + it }
    )
}

fun DeviceContact.toListOfContactCreationRequestDto(): List<ContactCreationRequestDto> {
    return phoneNumbers.map { phone ->
        ContactCreationRequestDto(
            firstName = firstName.orEmpty(),
            lastName = lastName.orEmpty(),
            phone = phone
        )
    }
}

fun List<DeviceContact>.toListOfContactCreationRequestDto(): List<ContactCreationRequestDto> {
    return flatMap(DeviceContact::toListOfContactCreationRequestDto)
}

private const val BASE_CONTACT_IMAGE_URL =
    "https://menastorage.fra1.cdn.digitaloceanspaces.com/identity/profile/image/"
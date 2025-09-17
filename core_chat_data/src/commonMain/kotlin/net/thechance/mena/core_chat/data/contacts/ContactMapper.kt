package net.thechance.mena.core_chat.data.contacts

import net.thechance.mena.core_chat.data.contacts.dto.ContactCreationRequestDto
import net.thechance.mena.core_chat.data.contacts.dto.ContactDto
import net.thechance.mena.core_chat.data.shared.dto.PagedDataDto
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.exception.ContactsFetchFailedException
import net.thechance.mena.core_chat.domain.model.PagedData
import com.bilalazzam.contacts_provider.Contact as DeviceContact

fun PagedDataDto<ContactDto>?.toPagedListOfContacts(): PagedData<Contact> {
    val pagedData = this ?: throw ContactsFetchFailedException("Response body is null")
    return PagedData(
        data = pagedData.data.orEmpty().toListOfContact(),
        totalItems = pagedData.totalItems ?: 0,
        isLastPage = (pagedData.pageNumber ?: 0) >= (pagedData.totalPages ?: 0)
    )
}

private fun List<ContactDto>.toListOfContact(): List<Contact> {
    return map { it.toDomain() }
}

private fun ContactDto.toDomain(): Contact {
    val names = name?.trim()?.split(" ", limit = 2) ?: listOf()
    val firstName = names.getOrNull(0).orEmpty()
    val lastName = names.getOrNull(1).orEmpty()

    return Contact(
        firstName = firstName,
        lastName = lastName,
        phone = phoneNumber.orEmpty(),
        isMenaUser = isMenaMember == true,
        imageUrl = imageUrl
    )
}

private fun DeviceContact.toListOfContactCreationRequestDto(): List<ContactCreationRequestDto> {
    return phoneNumbers.map { phone ->
        ContactCreationRequestDto(
            name = listOfNotNull(firstName, lastName)
                .joinToString(" "),
            phone = phone
        )
    }
}

fun List<DeviceContact>.toListOfContactCreationRequestDto(): List<ContactCreationRequestDto> {
    return flatMap ( DeviceContact::toListOfContactCreationRequestDto )
}
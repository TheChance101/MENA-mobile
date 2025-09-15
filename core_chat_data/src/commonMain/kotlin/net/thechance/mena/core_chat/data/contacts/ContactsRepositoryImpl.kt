package net.thechance.mena.core_chat.data.contacts

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import net.thechance.mena.core_chat.data.contacts.dto.ContactDto
import net.thechance.mena.core_chat.data.contacts.source.remote.ContactSyncer
import net.thechance.mena.core_chat.data.network.ApiConstants.CONTACTS_ENDPOINT
import net.thechance.mena.core_chat.data.shared.BaseRepository
import net.thechance.mena.core_chat.data.shared.dto.BaseResponseDto
import net.thechance.mena.core_chat.data.shared.dto.PagedDataDto
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.exception.ContactsFetchFailedException
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.ContactsRepository

class ContactsRepositoryImpl(
    private val client: HttpClient,
    private val contactSyncer : ContactSyncer
) : ContactsRepository, BaseRepository {


    override suspend fun getUserContacts(pageNumber: Int, pageSize: Int): PagedData<Contact> {
        return tryNetworkCall(
            defaultException = { ContactsFetchFailedException("Couldn't get user contacts", it) }) {
            client.get(CONTACTS_ENDPOINT) {
                parameter("pageNumber", pageNumber)
                parameter("pageSize", pageSize)
            }.body<BaseResponseDto<PagedDataDto<ContactDto>>>()
        }.toPagedListOfContacts()
    }

    override suspend fun syncContacts() {
        contactSyncer.sync()
    }

}
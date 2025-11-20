package net.thechance.mena.core_chat.domain.repository

import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.model.PagedData
import kotlin.uuid.ExperimentalUuidApi

interface ContactsRepository {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun getUserContacts(pageNumber: Int): PagedData<Contact>
    suspend fun syncContacts()
    suspend fun getHasUserSyncedContactsStatus(): Boolean
    suspend fun setHasUserSyncedContactsStatus(isSynced: Boolean)
    @OptIn(ExperimentalUuidApi::class)
    suspend fun getContactsByName(name: String, pageNumber: Int, isMenaUser: Boolean = true): PagedData<Contact>
}
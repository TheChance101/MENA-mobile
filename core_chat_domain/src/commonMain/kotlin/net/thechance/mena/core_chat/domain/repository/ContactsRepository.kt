package net.thechance.mena.core_chat.domain.repository

import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.model.PagedData

interface ContactsRepository {
    suspend fun getUserContacts(pageNumber: Int, pageSize: Int): PagedData<Contact>
    suspend fun syncContacts()
    suspend fun getUserSyncedState(): Boolean
    suspend fun setUserSyncedState(state: Boolean)
}
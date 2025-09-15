package net.thechance.mena.core_chat.data.contacts

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import net.thechance.mena.core_chat.data.contacts.dto.ContactDto
import net.thechance.mena.core_chat.data.contacts.source.remote.ContactSyncer
import net.thechance.mena.core_chat.data.network.ApiConstants.CONTACTS_ENDPOINT
import net.thechance.mena.core_chat.data.shared.BaseRepository
import net.thechance.mena.core_chat.data.shared.dto.BaseResponseDto
import net.thechance.mena.core_chat.data.shared.dto.PagedDataDto
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.exception.ContactSyncFailedException
import net.thechance.mena.core_chat.domain.exception.ContactsFetchFailedException
import net.thechance.mena.core_chat.domain.exception.DataStoreException
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.ContactsRepository

class ContactsRepositoryImpl(
    private val client: HttpClient,
    private val contactSyncer: ContactSyncer,
    private val dataStore: DataStore<Preferences>
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
        tryCall({ ContactSyncFailedException("Couldn't sync user contacts", it) }) {
            contactSyncer.sync()
        }
    }

    override suspend fun getUserSyncedState(): Boolean {
        return tryCall(
            defaultException = { DataStoreException("error with data store", it) }) {
            dataStore.data.map {
                it[USER_SYNCED_STATE_KEY]
            }.firstOrNull() == true
        }
    }

    override suspend fun setUserSyncedState(state: Boolean) {
        return tryCall(
            defaultException = { DataStoreException("error with data store", it) }) {
            dataStore.edit { preferences ->
                preferences[USER_SYNCED_STATE_KEY] = state
            }
        }
    }

    private companion object {
        val USER_SYNCED_STATE_KEY = booleanPreferencesKey("user_synced_state_key")

    }
}
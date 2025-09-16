package net.thechance.mena.core_chat.data.contacts

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.bilalazzam.contacts_provider.ContactField.FIRST_NAME
import com.bilalazzam.contacts_provider.ContactField.ID
import com.bilalazzam.contacts_provider.ContactField.LAST_NAME
import com.bilalazzam.contacts_provider.ContactField.PHONE_NUMBERS
import com.bilalazzam.contacts_provider.ContactsProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import net.thechance.mena.core_chat.data.contacts.dto.ContactDto
import net.thechance.mena.core_chat.data.network.ApiConstants.CONTACTS_ENDPOINT
import net.thechance.mena.core_chat.data.network.ApiConstants.SYNC_CONTACTS_ENDPOINT
import net.thechance.mena.core_chat.data.shared.BaseRepository
import net.thechance.mena.core_chat.data.shared.dto.BaseResponseDto
import net.thechance.mena.core_chat.data.shared.dto.PagedDataDto
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.exception.ContactSyncFailedException
import net.thechance.mena.core_chat.domain.exception.ContactsFetchFailedException
import net.thechance.mena.core_chat.domain.exception.UserSyncedStateException
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import com.bilalazzam.contacts_provider.Contact as DeviceContact


class ContactsRepositoryImpl(
    private val client: HttpClient,
    private val contactsProvider: ContactsProvider,
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
        tryNetworkCall(
            defaultException = { ContactSyncFailedException("Couldn't sync user contacts", it) }) {
            val contacts = getDeviceContacts()
            client.post(SYNC_CONTACTS_ENDPOINT) {
                setBody(contacts.toListOfContactCreationRequestDto())
            }.body<BaseResponseDto<Unit>>()
        }
    }

    private suspend fun getDeviceContacts(): List<DeviceContact> {
        return contactsProvider.getAllContacts(
            fields = setOf(ID, FIRST_NAME, LAST_NAME, PHONE_NUMBERS)
        )
    }

    override suspend fun getUserSyncedState(): Boolean {
        return tryCall(
            defaultException = { UserSyncedStateException("Couldn't get user synced state", it) }) {
            dataStore.data.map {
                it[USER_SYNCED_STATE_KEY]
            }.firstOrNull() == true
        }
    }

    override suspend fun setUserSyncedState(state: Boolean) {
        return tryCall(
            defaultException = { UserSyncedStateException("Couldn't set user synced state", it) }) {
            dataStore.edit { preferences ->
                preferences[USER_SYNCED_STATE_KEY] = state
            }
        }
    }

    private companion object {
        val USER_SYNCED_STATE_KEY = booleanPreferencesKey("user_synced_state_key")

    }
}
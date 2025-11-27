package net.thechance.mena.core_chat.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.bilalazzam.contacts_provider.ContactField
import com.bilalazzam.contacts_provider.ContactsProvider
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import net.thechance.mena.core_chat.data.source.remote.dto.ContactDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toListOfContactCreationRequestDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toPagedListOfContacts
import net.thechance.mena.core_chat.data.source.remote.network.tryNetworkCall
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.exception.ContactSyncFailedException
import net.thechance.mena.core_chat.domain.exception.ContactsFetchFailedException
import net.thechance.mena.core_chat.domain.exception.DataStoreException
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import kotlin.uuid.ExperimentalUuidApi
import net.thechance.mena.core_chat.data.source.local.datastore.tryCall
import net.thechance.mena.core_chat.data.source.remote.network.HttpClientHolder

class ContactsRepositoryImpl(
    private val clientHolder: HttpClientHolder,
    private val contactsProvider: ContactsProvider,
    private val dataStore: DataStore<Preferences>
) : ContactsRepository{

    private val client: HttpClient
        get() = clientHolder.getClient()

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getUserContacts(pageNumber: Int): PagedData<Contact> {
        return tryNetworkCall<PagedDataDto<ContactDto>>(
            defaultException = ContactsFetchFailedException("Couldn't get user contacts") ,
            bodyType = typeInfo<PagedDataDto<ContactDto>>()
        ) {
            client.get(CONTACTS_ENDPOINT) {
                parameter(PAGE_NUMBER_PARAMETER, pageNumber)
                parameter(PAGE_SIZE_PARAMETER, PAGE_SIZE)
            }
        }.toPagedListOfContacts()
    }

    override suspend fun syncContacts() {
        tryNetworkCall<Unit>(
            defaultException = ContactSyncFailedException("Couldn't sync user contacts") ,
            bodyType = typeInfo<Unit>()
        ) {
            val contacts = getDeviceContacts()
            client.post(SYNC_CONTACTS_ENDPOINT) {
                setBody(contacts.toListOfContactCreationRequestDto())
            }
        }
    }

    private suspend fun getDeviceContacts(): List<com.bilalazzam.contacts_provider.Contact> {
        return contactsProvider.getAllContacts(
            fields = setOf(
                ContactField.ID,
                ContactField.FIRST_NAME,
                ContactField.LAST_NAME,
                ContactField.PHONE_NUMBERS
            )
        )
    }

    override suspend fun getHasUserSyncedContactsStatus(): Boolean {
        return tryCall(
            defaultException = DataStoreException("error with data store") ) {
            dataStore.data.map {
                it[USER_SYNCED_STATE_KEY]
            }.firstOrNull() == true
        }
    }

    override suspend fun setHasUserSyncedContactsStatus(isSynced: Boolean) {
        return tryCall(
            defaultException = DataStoreException("error with data store") ) {
            dataStore.edit { preferences ->
                preferences[USER_SYNCED_STATE_KEY] = isSynced
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getContactsByName(
        name: String,
        pageNumber: Int,
        isMenaUser: Boolean
    ): PagedData<Contact> {
        return tryNetworkCall<PagedDataDto<ContactDto>>(
            defaultException = ContactsFetchFailedException("Couldn't get contacts") ,
            bodyType = typeInfo<PagedDataDto<ContactDto>>()
        ) {
            client.get(SEARCH_CONTACTS_ENDPOINT) {
                parameter(CONTACT_NAME_PARAMETER,name)
                parameter(ONLY_MENA_USER_PARAMETER,isMenaUser)
                parameter(PAGE_NUMBER_PARAMETER, pageNumber)
                parameter(PAGE_SIZE_PARAMETER, PAGE_SIZE)
            }
        }.toPagedListOfContacts()
    }

    private companion object {
        val USER_SYNCED_STATE_KEY = booleanPreferencesKey("user_synced_state_key")
        const val PAGE_NUMBER_PARAMETER = "page"
        const val PAGE_SIZE_PARAMETER = "size"
        const val CONTACT_NAME_PARAMETER = "query"
        const val ONLY_MENA_USER_PARAMETER = "onlyMenaUsers"
        const val PAGE_SIZE = 20
        const val CONTACTS_ENDPOINT = "/chat/contacts"
        const val SYNC_CONTACTS_ENDPOINT = "/chat/contacts/sync"
        const val SEARCH_CONTACTS_ENDPOINT = "/chat/contacts/search"
    }
}
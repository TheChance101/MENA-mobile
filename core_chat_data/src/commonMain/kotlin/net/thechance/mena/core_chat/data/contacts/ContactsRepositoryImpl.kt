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
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import net.thechance.mena.core_chat.data.contacts.dto.ContactDto
import net.thechance.mena.core_chat.data.network.ApiConstants.CONTACTS_ENDPOINT
import net.thechance.mena.core_chat.data.network.ApiConstants.SYNC_CONTACTS_ENDPOINT
import net.thechance.mena.core_chat.data.shared.BaseRepository
import net.thechance.mena.core_chat.data.shared.dto.PagedDataDto
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.exception.ContactSyncFailedException
import net.thechance.mena.core_chat.domain.exception.ContactsFetchFailedException
import net.thechance.mena.core_chat.domain.exception.DataStoreException
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import kotlin.uuid.ExperimentalUuidApi
import com.bilalazzam.contacts_provider.Contact as DeviceContact

class ContactsRepositoryImpl(
    private val client: HttpClient,
    private val authenticationRepository: AuthenticationRepository,
    private val contactsProvider: ContactsProvider,
    private val dataStore: DataStore<Preferences>
) : ContactsRepository, BaseRepository {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getUserContacts(pageNumber: Int): PagedData<Contact> {
        return tryNetworkCall<PagedDataDto<ContactDto>>(
            defaultException = { ContactsFetchFailedException("Couldn't get user contacts", it) },
            bodyType = typeInfo<PagedDataDto<ContactDto>>()
        ) {
            authenticationRepository.getAccessToken().let { token ->
                client.get(CONTACTS_ENDPOINT) {
                    parameter(PAGE_NUMBER_PARAMETER, pageNumber)
                    parameter(PAGE_SIZE_PARAMETER, PAGE_SIZE)
                    bearerAuth(token)
                }
            }
        }.toPagedListOfContacts()
    }

    override suspend fun syncContacts() {
        tryNetworkCall<Unit>(
            defaultException = { ContactSyncFailedException("Couldn't sync user contacts", it) },
            bodyType = typeInfo<Unit>()
        ) {
            val contacts = getDeviceContacts()
            authenticationRepository.getAccessToken().let { token ->
                client.post(SYNC_CONTACTS_ENDPOINT) {
                    setBody(contacts.toListOfContactCreationRequestDto())
                    bearerAuth(token)
                }
            }
        }
    }

    private suspend fun getDeviceContacts(): List<DeviceContact> {
        return contactsProvider.getAllContacts(
            fields = setOf(ID, FIRST_NAME, LAST_NAME, PHONE_NUMBERS)
        )
    }

    override suspend fun getSyncStatus(): Boolean {
        return tryCall(
            defaultException = { DataStoreException("error with data store", it) }) {
            dataStore.data.map {
                it[USER_SYNCED_STATE_KEY]
            }.firstOrNull() == true
        }
    }

    override suspend fun setSyncStatus(state: Boolean) {
        return tryCall(
            defaultException = { DataStoreException("error with data store", it) }) {
            dataStore.edit { preferences ->
                preferences[USER_SYNCED_STATE_KEY] = state
            }
        }
    }

    private companion object {
        val USER_SYNCED_STATE_KEY = booleanPreferencesKey("user_synced_state_key")
        const val PAGE_NUMBER_PARAMETER = "page"
        const val PAGE_SIZE_PARAMETER = "size"
        const val PAGE_SIZE = 20
    }
}
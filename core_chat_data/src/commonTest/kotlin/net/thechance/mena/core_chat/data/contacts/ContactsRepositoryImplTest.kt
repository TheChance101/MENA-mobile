@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.contacts

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import net.thechance.mena.core_chat.data.contacts.fakes.FakeContactsProvider
import net.thechance.mena.core_chat.data.contacts.fakes.FakeDataStore
import net.thechance.mena.core_chat.data.contacts.fakes.createPagedDataDto
import net.thechance.mena.core_chat.data.contacts.fakes.sampleContact
import net.thechance.mena.core_chat.data.contacts.fakes.sampleDeviceContact
import net.thechance.mena.core_chat.data.createHttpClient
import net.thechance.mena.core_chat.data.createRepository
import net.thechance.mena.core_chat.data.mockErrorPagedResponse
import net.thechance.mena.core_chat.data.mockSuccessPagedResponse
import net.thechance.mena.core_chat.data.repository.ContactsRepositoryImpl
import net.thechance.mena.core_chat.data.source.remote.dto.ContactDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.domain.exception.ContactSyncFailedException
import net.thechance.mena.core_chat.domain.exception.ContactsFetchFailedException
import net.thechance.mena.core_chat.domain.exception.ContactsPermissionDeniedException
import net.thechance.mena.core_chat.domain.exception.DataStoreException
import net.thechance.mena.core_chat.domain.exception.NoInternetException
import net.thechance.mena.core_chat.domain.exception.UnAuthorizedException
import net.thechance.mena.core_chat.domain.exception.UnknownException
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.uuid.ExperimentalUuidApi


class ContactsRepositoryImplTest {
    private val mockContactsProvider = FakeContactsProvider()
    private val mockDataStore = FakeDataStore()
    private val authenticationRepository = mock<AuthenticationRepository>()
    private lateinit var httpClient: HttpClient
    private lateinit var repository: ContactsRepositoryImpl

    @BeforeTest
    fun setUp() {
        mockContactsProvider.reset()
        mockDataStore.reset()
        httpClient = createHttpClient()

        everySuspend { authenticationRepository.getAccessToken() } returns "token"

        repository = createRepository(mockContactsProvider, mockDataStore)
    }

    @Test
    fun `should return paged contacts when getUserContacts is called successfully`() = runTest {

        val result = repository.getUserContacts(pageNumber = 1)

        assertThat(result.data).isEqualTo(
            listOf(
                sampleContact
            )
        )
    }


    @Test
    fun `should return empty paged contacts when getUserContacts returns no data`() = runTest {

        repository = createRepository(
            contactsProvider = mockContactsProvider,
            contactsDataStore = mockDataStore,
            contactsResponse = {
                mockSuccessPagedResponse<PagedDataDto<ContactDto>>(
                    body = PagedDataDto(
                        data = emptyList(),
                        pageNumber = 1,
                        pageSize = 10,
                        totalItems = 0,
                        totalPages = 1
                    )
                )
            }
        )


        val result = repository.getUserContacts(pageNumber = 1)


        assertThat(result.data).isEmpty()
    }

    @Test
    fun `should throw UnAuthorizedException when getUserContacts receives unauthorized response`() =
        runTest {

            repository = createRepository(
                contactsProvider = mockContactsProvider,
                contactsDataStore = mockDataStore,
                contactsResponse = {
                    mockErrorPagedResponse<PagedDataDto<ContactDto>>(
                        status = HttpStatusCode.Unauthorized
                    )
                }
            )

            assertFailsWith<UnAuthorizedException> {
                repository.getUserContacts(pageNumber = 1)
            }
        }

    @Test
    fun `should throw UnknownException when getUserContacts receives error response`() = runTest {

        repository = createRepository(
            contactsProvider = mockContactsProvider,
            contactsDataStore = mockDataStore,
            contactsResponse = {
                mockErrorPagedResponse<PagedDataDto<ContactDto>>(
                    status = HttpStatusCode.InternalServerError
                )
            }
        )


        assertFailsWith<UnknownException> {
            repository.getUserContacts(pageNumber = 1)
        }
    }

    @Test
    fun `should throw ContactsFetchFailedException when getUserContacts encounters network error`() =
        runTest {

            repository = createRepository(
                contactsProvider = mockContactsProvider,
                contactsDataStore = mockDataStore,
                contactsResponse = {
                    throw IOException()
                }
            )


            assertFailsWith<NoInternetException> {
                repository.getUserContacts(pageNumber = 1)
            }
        }

    @Test
    fun `should call contacts provider when syncContacts is called`() = runTest {

        mockContactsProvider.setContacts(
            listOf(
                sampleDeviceContact
            )
        )


        repository.syncContacts()


        assertThat(mockContactsProvider.getAllContactsCalled).isTrue()
    }

    @Test
    fun `should throw ContactSyncFailedException when syncContacts encounters network error`() =
        runTest {

            repository = createRepository(
                contactsProvider = mockContactsProvider,
                contactsDataStore = mockDataStore,
                syncContactsResponse = {
                    throw IOException()
                }
            )


            assertFailsWith<NoInternetException> {
                repository.syncContacts()
            }
        }

    @Test
    fun `should throw UnAuthorizedException when syncContacts receives failed response`() =
        runTest {

            repository = createRepository(
                contactsProvider = mockContactsProvider,
                contactsDataStore = mockDataStore,
                syncContactsResponse = {
                    mockErrorPagedResponse<PagedDataDto<ContactDto>>(
                        status = HttpStatusCode.Unauthorized
                    )
                }
            )


            assertFailsWith<UnAuthorizedException> {
                repository.syncContacts()
            }
        }


    @Test
    fun `should return false when getHasUserSyncedContactsStatus is called and state is not set`() = runTest {

        val result = repository.getHasUserSyncedContactsStatus()


        assertThat(result).isFalse()
    }

    @Test
    fun `should return true when getHasUserSyncedContactsStatus is called and state is true`() = runTest {

        mockDataStore.setSyncStatus(true)


        val result = repository.getHasUserSyncedContactsStatus()


        assertThat(result).isTrue()
    }

    @Test
    fun `should return false when getHasUserSyncedContactsStatus is called and state is false`() = runTest {

        mockDataStore.setSyncStatus(false)


        val result = repository.getHasUserSyncedContactsStatus()


        assertThat(result).isFalse()
    }

    @Test
    fun `should throw DataStoreException when getHasUserSyncedContactsStatus encounters error`() = runTest {

        mockDataStore.throwOnRead = true


        assertFailsWith<DataStoreException> {
            repository.getHasUserSyncedContactsStatus()
        }
    }

    @Test
    fun `should set user synced state to true when setHasUserSyncedContactsStatus is called with true`() =
        runTest {

            repository.setHasUserSyncedContactsStatus(true)


            assertThat(mockDataStore.getSyncStatus()).isTrue()
        }

    @Test
    fun `should set user synced state to false when setHasUserSyncedContactsStatus is called with false`() =
        runTest {

            repository.setHasUserSyncedContactsStatus(false)


            assertThat(mockDataStore.getSyncStatus()).isFalse()
        }

    @Test
    fun `should throw DataStoreException when setHasUserSyncedContactsStatus encounters error`() = runTest {

        mockDataStore.throwOnUpdate = true


        assertFailsWith<DataStoreException> {
            repository.setHasUserSyncedContactsStatus(true)
        }
    }

    @Test
    fun `should return true when getHasUserSyncedContactsStatus is set to true`() = runTest {

        mockDataStore.setSyncStatus(true)


        val result = repository.getHasUserSyncedContactsStatus()


        assertThat(result).isTrue()
    }

    @Test
    fun `should return false when getHasUserSyncedContactsStatus is set to false`() = runTest {

        mockDataStore.setSyncStatus(false)


        val result = repository.getHasUserSyncedContactsStatus()


        assertThat(result).isFalse()
    }

    @Test
    fun `should return false when getHasUserSyncedContactsStatus is not set`() = runTest {

        val result = repository.getHasUserSyncedContactsStatus()


        assertThat(result).isFalse()
    }

    @Test
    fun `should set synced state to true when setHasUserSyncedContactsStatus is called with true`() = runTest {

        repository.setHasUserSyncedContactsStatus(true)


        assertThat(mockDataStore.getSyncStatus()).isTrue()
    }

    @Test
    fun `should set synced state to false when setHasUserSyncedContactsStatus is called with false`() =
        runTest {

            repository.setHasUserSyncedContactsStatus(false)


            assertThat(mockDataStore.getSyncStatus()).isFalse()
        }

    @Test
    fun `should return right pageSize when getUserContacts is called and response contains data`() =
        runTest {
            val page1Contacts = createPagedDataDto(
                data = List(10) { i ->
                    ContactDto(
                        firstName = "Page1",
                        lastName = "User$i",
                        phoneNumber = "010000000$i",
                        menaUserId = null,
                        imageUrl = null
                    )
                },
                pageNumber = 1,
                pageSize = 10,
                totalItems = 15,
                totalPages = 2
            )

            repository = createRepository(
                contactsProvider = mockContactsProvider,
                contactsDataStore = mockDataStore,
                contactsResponse = {
                    mockSuccessPagedResponse(
                        body = page1Contacts
                    )
                }
            )

            val result = repository.getUserContacts(pageNumber = 1)

            assertThat(result.data).isEqualTo(page1Contacts.data?.map { it.toDomain() })
        }

    @Test
    fun `should return empty list when getUserContacts requests page beyond total pages`() =
        runTest {
            repository = createRepository(
                contactsProvider = mockContactsProvider,
                contactsDataStore = mockDataStore,
                contactsResponse = {
                    mockSuccessPagedResponse<PagedDataDto<ContactDto>>(
                        body = PagedDataDto(
                            data = emptyList(),
                            pageNumber = 3,
                            pageSize = 10,
                            totalItems = 15,
                            totalPages = 2
                        )
                    )
                }
            )

            val result = repository.getUserContacts(pageNumber = 3)

            assertThat(result.data).isEmpty()
        }

}

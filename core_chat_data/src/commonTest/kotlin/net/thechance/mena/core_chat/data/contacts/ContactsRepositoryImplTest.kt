@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.contacts

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import net.thechance.mena.core_chat.data.contacts.fakes.FakeContactsProvider
import net.thechance.mena.core_chat.data.contacts.fakes.FakeDataStore
import net.thechance.mena.core_chat.data.contacts.fakes.createPagedDataDto
import net.thechance.mena.core_chat.data.contacts.fakes.sampleContact
import net.thechance.mena.core_chat.data.contacts.fakes.sampleDeviceContact
import net.thechance.mena.core_chat.data.createContactsRepository
import net.thechance.mena.core_chat.data.createHttpClient
import net.thechance.mena.core_chat.data.mockErrorPagedResponse
import net.thechance.mena.core_chat.data.mockSuccessPagedResponse
import net.thechance.mena.core_chat.data.repository.ContactsRepositoryImpl
import net.thechance.mena.core_chat.data.source.remote.dto.ContactDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.source.remote.network.HttpClientHolder
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
    private lateinit var httpClientHolder: HttpClientHolder
    private lateinit var repository: ContactsRepositoryImpl

    @BeforeTest
    fun setUp() {
        mockContactsProvider.reset()
        mockDataStore.reset()
        httpClientHolder = mock<HttpClientHolder>()

        everySuspend { authenticationRepository.getAccessToken() } returns "token"
        every { httpClientHolder.getClient() } returns createHttpClient()

        repository = createContactsRepository(
            contactsProvider = mockContactsProvider,
            contactsDataStore = mockDataStore,
            httpClientHolder = httpClientHolder
        )
    }

    @Test
    fun `should return paged contacts when getUserContacts is called successfully`() = runTest {

        val result = repository.getUserContacts(pageNumber = 1)

        assertThat(result.data).isEqualTo(listOf(sampleContact))
    }


    @Test
    fun `should return empty paged contacts when getUserContacts returns no data`() = runTest {
        every { httpClientHolder.getClient() } returns createHttpClient(
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
            },
        )
        repository = createContactsRepository(
            contactsProvider = mockContactsProvider,
            contactsDataStore = mockDataStore,
            httpClientHolder = httpClientHolder
        )


        val result = repository.getUserContacts(pageNumber = 1)


        assertThat(result.data).isEmpty()
    }

    @Test
    fun `should throw UnAuthorizedException when getUserContacts receives unauthorized response`() =
        runTest {
            every { httpClientHolder.getClient() } returns createHttpClient(
                contactsResponse = {
                    mockErrorPagedResponse<PagedDataDto<ContactDto>>(
                        status = HttpStatusCode.Unauthorized
                    )
                },
            )
            repository = createContactsRepository(
                contactsProvider = mockContactsProvider,
                contactsDataStore = mockDataStore,
                httpClientHolder = httpClientHolder
            )

            assertFailsWith<UnAuthorizedException> {
                repository.getUserContacts(pageNumber = 1)
            }
        }

    @Test
    fun `should throw UnknownException when getUserContacts receives error response`() = runTest {
        every { httpClientHolder.getClient() } returns createHttpClient(
            contactsResponse = {
                mockErrorPagedResponse<PagedDataDto<ContactDto>>(
                    status = HttpStatusCode.InternalServerError
                )
            },
        )
        repository = createContactsRepository(
            contactsProvider = mockContactsProvider,
            contactsDataStore = mockDataStore,
            httpClientHolder = httpClientHolder
        )


        assertFailsWith<UnknownException> {
            repository.getUserContacts(pageNumber = 1)
        }
    }

    @Test
    fun `should throw ContactsFetchFailedException when getUserContacts encounters network error`() =
        runTest {
            every { httpClientHolder.getClient() } returns createHttpClient(
                contactsResponse = { throw IOException() },
            )

            repository = createContactsRepository(
                contactsProvider = mockContactsProvider,
                contactsDataStore = mockDataStore,
                httpClientHolder = httpClientHolder
            )


            assertFailsWith<NoInternetException> {
                repository.getUserContacts(pageNumber = 1)
            }
        }

    @Test
    fun `should call contacts provider when syncContacts is called`() = runTest {

        mockContactsProvider.setContacts(
            listOf(sampleDeviceContact)
        )


        repository.syncContacts()


        assertThat(mockContactsProvider.getAllContactsCalled).isTrue()
    }

    @Test
    fun `should throw ContactSyncFailedException when syncContacts encounters network error`() =
        runTest {
            every { httpClientHolder.getClient() } returns createHttpClient(
                syncContactsResponse = {
                    throw IOException()
                },
            )
            repository = createContactsRepository(
                contactsProvider = mockContactsProvider,
                contactsDataStore = mockDataStore,
                httpClientHolder = httpClientHolder
            )


            assertFailsWith<NoInternetException> {
                repository.syncContacts()
            }
        }

    @Test
    fun `should throw UnAuthorizedException when syncContacts receives failed response`() =
        runTest {
            every { httpClientHolder.getClient() } returns createHttpClient(
                syncContactsResponse = {
                    mockErrorPagedResponse<PagedDataDto<ContactDto>>(
                        status = HttpStatusCode.Unauthorized
                    )
                },
            )
            repository = createContactsRepository(
                contactsProvider = mockContactsProvider,
                contactsDataStore = mockDataStore,
                httpClientHolder = httpClientHolder
            )


            assertFailsWith<UnAuthorizedException> {
                repository.syncContacts()
            }
        }


    @Test
    fun `should return false when getHasUserSyncedContactsStatus is called and state is not set`() =
        runTest {

            val result = repository.getHasUserSyncedContactsStatus()


            assertThat(result).isFalse()
        }

    @Test
    fun `should return true when getHasUserSyncedContactsStatus is called and state is true`() =
        runTest {

            mockDataStore.setSyncStatus(true)


            val result = repository.getHasUserSyncedContactsStatus()


            assertThat(result).isTrue()
        }

    @Test
    fun `should return false when getHasUserSyncedContactsStatus is called and state is false`() =
        runTest {

            mockDataStore.setSyncStatus(false)


            val result = repository.getHasUserSyncedContactsStatus()


            assertThat(result).isFalse()
        }

    @Test
    fun `should throw DataStoreException when getHasUserSyncedContactsStatus encounters error`() =
        runTest {

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
    fun `should throw DataStoreException when setHasUserSyncedContactsStatus encounters error`() =
        runTest {

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
    fun `should set synced state to true when setHasUserSyncedContactsStatus is called with true`() =
        runTest {

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
            every { httpClientHolder.getClient() } returns createHttpClient(
                contactsResponse = {
                    mockSuccessPagedResponse(
                        body = page1Contacts
                    )
                },
            )

            repository = createContactsRepository(
                contactsProvider = mockContactsProvider,
                contactsDataStore = mockDataStore,
                httpClientHolder = httpClientHolder
            )

            val result = repository.getUserContacts(pageNumber = 1)

            assertThat(result.data).isEqualTo(page1Contacts.data.map { it.toDomain() })
        }

    @Test
    fun `should return empty list when getUserContacts requests page beyond total pages`() =
        runTest {
            every { httpClientHolder.getClient() } returns createHttpClient(
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
                },
            )
            repository = createContactsRepository(
                contactsProvider = mockContactsProvider,
                contactsDataStore = mockDataStore,
                httpClientHolder = httpClientHolder
            )

            val result = repository.getUserContacts(pageNumber = 3)

            assertThat(result.data).isEmpty()
        }

    @Test
    fun `should return paged contacts when getContactsByName is called successfully`() = runTest {
        val searchName = "John"
        val pageNumber = 1
        val isMenaUser = true
        every { httpClientHolder.getClient() } returns createHttpClient(
            contactsResponse = {
                mockSuccessPagedResponse(
                    PagedDataDto(
                        data = listOf(
                            ContactDto(
                                firstName = "John",
                                lastName = "Doe",
                                phoneNumber = "0100000001",
                                menaUserId = null,
                                imageUrl = null
                            )
                        ),
                        pageNumber = pageNumber,
                        pageSize = 20,
                        totalItems = 1,
                        totalPages = 1
                    )
                )
            },
        )

        repository = createContactsRepository(
            contactsProvider = mockContactsProvider,
            contactsDataStore = mockDataStore,
            httpClientHolder = httpClientHolder
        )

        val result = repository.getContactsByName(
            name = searchName,
            pageNumber = pageNumber,
            isMenaUser = isMenaUser
        )

        assertThat(result.data).isEqualTo(
            listOf(
                ContactDto(
                    firstName = "John",
                    lastName = "Doe",
                    phoneNumber = "0100000001",
                    menaUserId = null,
                    imageUrl = null
                ).toDomain()
            )
        )
    }

    @Test
    fun `should return empty paged contacts when getContactsByName returns no data`() = runTest {
        val searchName = "Unknown"
        val pageNumber = 1
        val isMenaUser = true
        every { httpClientHolder.getClient() } returns createHttpClient(
            contactsResponse = {
                mockSuccessPagedResponse<ContactDto>(
                    PagedDataDto(
                        data = emptyList(),
                        pageNumber = pageNumber,
                        pageSize = 20,
                        totalItems = 0,
                        totalPages = 1
                    )
                )
            },
        )

        repository = createContactsRepository(
            contactsProvider = mockContactsProvider,
            contactsDataStore = mockDataStore,
            httpClientHolder = httpClientHolder
        )

        val result = repository.getContactsByName(
            name = searchName,
            pageNumber = pageNumber,
            isMenaUser = isMenaUser
        )

        assertThat(result.data).isEmpty()
    }

    @Test
    fun `should throw UnAuthorizedException when getContactsByName receives unauthorized response`() =
        runTest {
            val searchName = "John"
            val pageNumber = 1
            val isMenaUser = true
            every { httpClientHolder.getClient() } returns createHttpClient(
                contactsResponse = {
                    mockErrorPagedResponse<PagedDataDto<ContactDto>>(HttpStatusCode.Unauthorized)
                },
            )

            repository = createContactsRepository(
                contactsProvider = mockContactsProvider,
                contactsDataStore = mockDataStore,
                httpClientHolder = httpClientHolder
            )

            assertFailsWith<UnAuthorizedException> {
                repository.getContactsByName(
                    name = searchName,
                    pageNumber = pageNumber,
                    isMenaUser = isMenaUser
                )
            }
        }

    @Test
    fun `should throw UnknownException when getContactsByName receives server error`() = runTest {
        val searchName = "John"
        val pageNumber = 1
        val isMenaUser = true
        every { httpClientHolder.getClient() } returns createHttpClient(
            contactsResponse = {
                mockErrorPagedResponse<PagedDataDto<ContactDto>>(HttpStatusCode.InternalServerError)
            },
        )

        repository = createContactsRepository(
            contactsProvider = mockContactsProvider,
            contactsDataStore = mockDataStore,
            httpClientHolder = httpClientHolder
        )

        assertFailsWith<UnknownException> {
            repository.getContactsByName(
                name = searchName,
                pageNumber = pageNumber,
                isMenaUser = isMenaUser
            )
        }
    }

    @Test
    fun `should throw NoInternetException when getContactsByName encounters network error`() =
        runTest {
            val searchName = "John"
            val pageNumber = 1
            val isMenaUser = true
            every { httpClientHolder.getClient() } returns createHttpClient(
                contactsResponse = { throw IOException() },
            )

            repository = createContactsRepository(
                contactsProvider = mockContactsProvider,
                contactsDataStore = mockDataStore,
                httpClientHolder = httpClientHolder
            )

            assertFailsWith<NoInternetException> {
                repository.getContactsByName(
                    name = searchName,
                    pageNumber = pageNumber,
                    isMenaUser = isMenaUser
                )
            }
        }

}

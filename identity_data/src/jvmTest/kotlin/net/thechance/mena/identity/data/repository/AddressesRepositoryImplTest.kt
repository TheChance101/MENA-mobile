package net.thechance.mena.identity.data.repository

import assertk.assertFailure
import assertk.assertions.isInstanceOf
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.data.utils.mockHttpClient
import net.thechance.mena.identity.data.utils.mockHttpClientError
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.exception.UnAuthorizedException
import org.junit.Test

class AddressesRepositoryImplTest {

    lateinit var client: HttpClient
    lateinit var addressRepositoryImpl: AddressesRepositoryImpl

    @Test
    fun `createAddress() should not throw exception when server returns 200`() = runTest {
        client = mockHttpClient(Unit)

        addressRepositoryImpl = AddressesRepositoryImpl(client)

        addressRepositoryImpl.createAddress(fakeNewAddress)
    }

    @Test
    fun `editAddress() should not throw exception when server returns 200`() = runTest {
        client = mockHttpClient(Unit)

        addressRepositoryImpl = AddressesRepositoryImpl(client)

        addressRepositoryImpl.editAddress(fakeExistingAddress)
    }

    @Test
    fun `createAddress() should throw Unauthorized Exceptions when server returns 401`() = runTest {
        client = mockHttpClientError(HttpStatusCode.Unauthorized)

        addressRepositoryImpl = AddressesRepositoryImpl(client)

        assertFailure { addressRepositoryImpl.createAddress(fakeNewAddress) }
            .isInstanceOf<UnAuthorizedException>()
    }

    @Test
    fun `editAddress() should throw Unauthorized Exceptions when server returns 401`() = runTest {
        client = mockHttpClientError(HttpStatusCode.Unauthorized)

        addressRepositoryImpl = AddressesRepositoryImpl(client)

        assertFailure { addressRepositoryImpl.editAddress(fakeExistingAddress) }
            .isInstanceOf<UnAuthorizedException>()
    }


    val fakeNewAddress = Address(
        addressLine = "Cairo",
        addressType = "Home",
        latitude = 30.0444,
        longitude = 31.2357,
        otherAddressType = null,
        isActive = false
    )
    val fakeExistingAddress = Address(
        id = "123",
        addressLine = "Cairo",
        addressType = "Home",
        latitude = 30.0444,
        longitude = 31.2357,
        otherAddressType = null,
        isActive = false
    )

}
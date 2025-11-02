package net.thechance.mena.identity.data.repository

import assertk.assertFailure
import assertk.assertions.isInstanceOf
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.data.dto.addresses.response.AddressResponseDto
import net.thechance.mena.identity.data.repository.location.AddressesRepositoryImpl
import net.thechance.mena.identity.data.repository.location.GeocoderWrapper
import net.thechance.mena.identity.data.utils.mockHttpClient
import net.thechance.mena.identity.data.utils.mockHttpClientError
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.exception.AddressNotFoundException
import net.thechance.mena.identity.domain.exception.UnAuthorizedException
import net.thechance.mena.identity.domain.model.AddressInput
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import net.thechance.mena.identity.domain.model.Coordinates as DomainCoordinates

@OptIn(ExperimentalUuidApi::class)
class AddressesRepositoryImplTest {

    lateinit var client: HttpClient
    private val geocoder = mockk<GeocoderWrapper>()
    lateinit var addressRepositoryImpl: AddressesRepositoryImpl

    @Test
    fun `createAddress() should not throw exception when server returns 200`() = runTest {
        client = mockHttpClient(Unit)

        addressRepositoryImpl = AddressesRepositoryImpl(client, geocoder)

        addressRepositoryImpl.createAddress(fakeNewAddressInput)
    }

    @Test
    fun `updateAddress() should not throw exception when server returns 200`() = runTest {
        client = mockHttpClient(Unit)

        addressRepositoryImpl = AddressesRepositoryImpl(client, geocoder)

        addressRepositoryImpl.updateAddress(fakeAddressId, fakeNewAddressInput)
    }

    @Test
    fun `getUserAddresses() should not throw exception when server returns 200`() = runTest {
        client = mockHttpClient(listOf(fakeAddressResponseDto))

        addressRepositoryImpl = AddressesRepositoryImpl(client, geocoder)

        val result = addressRepositoryImpl.getUserAddresses()
        assertEquals(1, result.size)
    }

    @Test
    fun `deleteAddress() should not throw exception when server returns 200`() = runTest {
        client = mockHttpClient(Unit)

        addressRepositoryImpl = AddressesRepositoryImpl(client, geocoder)

        addressRepositoryImpl.deleteAddress(fakeAddressId)
    }

    @Test
    fun `createAddress() should throw Unauthorized Exceptions when server returns 401`() = runTest {
        client = mockHttpClientError(HttpStatusCode.Unauthorized)

        addressRepositoryImpl = AddressesRepositoryImpl(client, geocoder)

        assertFailure { addressRepositoryImpl.createAddress(fakeNewAddressInput) }
            .isInstanceOf<UnAuthorizedException>()
    }

    @Test
    fun `updateAddress() should throw Unauthorized Exceptions when server returns 401`() = runTest {
        client = mockHttpClientError(HttpStatusCode.Unauthorized)

        addressRepositoryImpl = AddressesRepositoryImpl(client, geocoder)

        assertFailure { addressRepositoryImpl.updateAddress(fakeAddressId, fakeNewAddressInput) }
            .isInstanceOf<UnAuthorizedException>()
    }

    @Test
    fun `getUserAddresses() should throw Unauthorized Exceptions when server returns 401`() =
        runTest {
            client = mockHttpClientError(HttpStatusCode.Unauthorized)

            addressRepositoryImpl = AddressesRepositoryImpl(client, geocoder)

            assertFailure { addressRepositoryImpl.getUserAddresses() }
                .isInstanceOf<UnAuthorizedException>()
        }


    @Test
    fun `deleteAddress() should throw Unauthorized Exceptions when server returns 401`() = runTest {
        client = mockHttpClientError(HttpStatusCode.Unauthorized)

        addressRepositoryImpl = AddressesRepositoryImpl(client, geocoder)

        assertFailure { addressRepositoryImpl.deleteAddress(Uuid.random()) }
            .isInstanceOf<UnAuthorizedException>()
    }


    @Test
    fun `getLocationName should return formatted address when the place is not valid`() =
        runTest {
            client = mockHttpClientError(HttpStatusCode.Unauthorized)
            val domainCoordinates = DomainCoordinates(LATITUDE, LONGITUDE)
            coEvery { geocoder.placeOrNull(any()) } returns place

            addressRepositoryImpl = AddressesRepositoryImpl(client, geocoder)
            val result = addressRepositoryImpl.getLocationName(domainCoordinates)

            assertEquals("Basra, Basra Governorate, Basra", result)
        }

    @Test
    fun `getLocationName should throw AddressNotFoundException when the place is null`() = runTest {
        val domainCoordinates = DomainCoordinates(LATITUDE, LONGITUDE)
        coEvery { geocoder.placeOrNull(any()) } returns null
        client = mockHttpClientError(HttpStatusCode.Unauthorized)

        addressRepositoryImpl = AddressesRepositoryImpl(client, geocoder)

        assertFailsWith<AddressNotFoundException> {
            addressRepositoryImpl.getLocationName(domainCoordinates)
        }
    }

    private companion object {
        const val LATITUDE = 28.0
        const val LONGITUDE = 29.0

        val place = Place(
            subAdministrativeArea = "Basra",
            administrativeArea = "Basra Governorate",
            country = "Basra",
            isoCountryCode = null,
            coordinates = Coordinates(LATITUDE, LONGITUDE),
            name = "",
            street = "",
            postalCode = "",
            locality = "",
            subLocality = "",
            thoroughfare = "",
            subThoroughfare = ""
        )
    }

    val fakeAddressId = Uuid.random()
    
    val fakeNewAddressInput = AddressInput(
        addressLine = "Cairo",
        addressType = AddressType.getAddressTypeFromString("Home"),
        latitude = 30.0444,
        longitude = 31.2357
    )
    
    val fakeAddressResponseDto = AddressResponseDto(
        id = fakeAddressId.toString(),
        addressLine = "Cairo",
        addressType = "Home",
        latitude = 30.0444,
        longitude = 31.2357,
        isActive = false
    )

}
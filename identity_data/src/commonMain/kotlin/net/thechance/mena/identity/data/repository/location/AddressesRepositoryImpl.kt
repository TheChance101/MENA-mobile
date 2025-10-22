package net.thechance.mena.identity.data.repository.location

import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.MobileGeolocator
import io.ktor.client.HttpClient
import net.thechance.mena.identity.data.dto.addresses.AddressRequestDto
import net.thechance.mena.identity.data.dto.addresses.AddressResponseDto
import net.thechance.mena.identity.data.mapper.toDto
import net.thechance.mena.identity.data.mapper.toEntity
import net.thechance.mena.identity.data.utils.deleteJson
import net.thechance.mena.identity.data.utils.getJson
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.data.utils.putJson
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.exception.AddressNotFoundException
import net.thechance.mena.identity.domain.exception.UnableToFindLocationException
import net.thechance.mena.identity.domain.model.AddressInput
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.domain.model.Coordinates
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddressesRepositoryImpl(
    private val client: HttpClient,
    private val geocoder: GeocoderWrapper,
) : AddressesRepository {

    private var activeAddress: Address? = null

    override suspend fun createAddress(addressInput: AddressInput) {
        return safeWrapper {
            client.postJson(
                requestDto = addressInput.toDto(),
                path = ADDRESS_ENDPOINT
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun updateAddress(addressId: Uuid, addressInput: AddressInput) {
        return safeWrapper {
            client.putJson(
                requestDto = addressInput.toDto(id = addressId.toString()),
                path = "$ADDRESS_ENDPOINT/$addressId"
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deleteAddress(addressId: Uuid) = safeWrapper {
        client.deleteJson(
            path = "$DELETE_LOCATION_ENDPOINT/$addressId",
            queryParams = mapOf(ADDRESS_ID to addressId.toString())
        )
    }

    override suspend fun getUserAddresses(): List<Address> {
        return safeWrapper<List<AddressResponseDto>> {
            client.getJson(ADDRESS_ENDPOINT)
        }.map { responseDto ->
            val address = responseDto.toEntity()
            if (responseDto.isActive) {
                activeAddress = address
            }
            address
        }
    }

    override suspend fun getActiveAddress(): Address? {
        return activeAddress
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun setActiveAddress(addressId: Uuid) {
        safeWrapper {
            val response: List<AddressResponseDto> = client.getJson(ADDRESS_ENDPOINT)
            val addressToActivate = response.find { it.id == addressId.toString() }?.toEntity()
            
            if (addressToActivate != null) {
                client.putJson<AddressRequestDto, Unit>(
                    requestDto = addressToActivate.toDto(id = addressId.toString(), isActive = true),
                    path = "$ADDRESS_ENDPOINT/$addressId"
                )
                activeAddress = addressToActivate
            }
        }
    }

    override suspend fun getCurrentLocation(): Coordinates? {
        val geolocator: Geolocator = MobileGeolocator()
        return when (val result = geolocator.current(Priority.HighAccuracy)) {
            is GeolocatorResult.Error -> {
                throw UnableToFindLocationException()
            }

            is GeolocatorResult.Success -> {
                val location = result.data
                Coordinates(
                    latitude = location.coordinates.latitude,
                    longitude = location.coordinates.longitude
                )
            }
        }
    }

    override suspend fun getLocationName(
        coordinates: Coordinates,
    ): String {
        val geocoder = geocoder.placeOrNull(coordinates)
        return geocoder?.let {
            listOfNotNull(it.subAdministrativeArea, it.administrativeArea, it.country)
                .joinToString(", ")
        }?: throw AddressNotFoundException()
    }

    companion object {
        const val ADDRESS_ENDPOINT = "identity/addresses"
        const val DELETE_LOCATION_ENDPOINT = "identity/addresses"
        const val ADDRESS_ID = "id"
    }
}
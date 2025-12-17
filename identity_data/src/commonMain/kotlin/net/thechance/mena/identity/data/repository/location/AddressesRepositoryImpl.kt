package net.thechance.mena.identity.data.repository.location

import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.MobileGeolocator
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.thechance.mena.identity.data.dataSource.local.database.dao.AddressDao
import net.thechance.mena.identity.data.dto.addresses.response.AddressResponseDto
import net.thechance.mena.identity.data.mapper.toDomain
import net.thechance.mena.identity.data.mapper.toDto
import net.thechance.mena.identity.data.mapper.toEntity
import net.thechance.mena.identity.data.mapper.toLocalEntity
import net.thechance.mena.identity.data.utils.deleteJson
import net.thechance.mena.identity.data.utils.getJson
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.data.utils.putJson
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.exception.AddressNotFoundException
import net.thechance.mena.identity.domain.exception.UnableToFindLocationException
import net.thechance.mena.identity.domain.model.AddressInput
import net.thechance.mena.identity.domain.model.Coordinates
import net.thechance.mena.identity.domain.repository.AddressesRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddressesRepositoryImpl(
    private val client: HttpClient,
    private val geocoder: GeocoderWrapper,
    private val addressDao: AddressDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val scope: CoroutineScope = CoroutineScope(dispatcher)
) : AddressesRepository {

    override suspend fun createAddress(addressInput: AddressInput) {
        return safeWrapper {
            val response: AddressResponseDto = client.postJson(
                requestDto = addressInput.toDto(),
                path = ADDRESS_ENDPOINT
            )
            if (response.isActive) {
                cacheActiveAddress(response)
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun updateAddress(
        addressId: Uuid,
        addressInput: AddressInput,
        isActive: Boolean
    ) {
        return safeWrapper {
            val response: AddressResponseDto = client.putJson(
                requestDto = addressInput.toDto(id = addressId.toString(), isActive),
                path = "$ADDRESS_ENDPOINT/$addressId"
            )
            if (isActive) {
                cacheActiveAddress(response)
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun deleteAddress(addressId: Uuid) = safeWrapper {
        client.deleteJson(
            path = "$DELETE_LOCATION_ENDPOINT/$addressId",
            queryParams = mapOf(ADDRESS_ID to addressId.toString())
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun clearAddresses() {
        addressDao.deleteActiveAddress()
    }

    override suspend fun getUserAddresses(): List<Address> {
        return safeWrapper<List<AddressResponseDto>> {
            client.getJson(ADDRESS_ENDPOINT)
        }.map { it.toEntity() }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun getActiveAddress(): Address? = withContext(dispatcher) {
        getLocalActiveAddress()?.also { revalidateActiveAddressAsync() }
            ?: fetchAndCacheRemoteActiveAddressOrNull()
    }

    private suspend fun getLocalActiveAddress(): Address? {
        return addressDao.getActiveAddress()?.toDomain()
    }

    private fun revalidateActiveAddressAsync() {
        scope.launch {
            runCatching {
                val remote = fetchRemoteActiveAddress()
                cacheActiveAddress(remote)
            }
        }
    }

    private suspend fun fetchRemoteActiveAddress(): AddressResponseDto {
        return safeWrapper { client.getJson(ACTIVE_ADDRESS_ENDPOINT) }
    }

    private suspend fun fetchAndCacheRemoteActiveAddressOrNull(): Address? {
        return runCatching {
            val remote = fetchRemoteActiveAddress()
            cacheActiveAddress(remote)
            remote.toEntity()
        }.getOrNull()
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun setActiveAddress(addressId: Uuid) {
        safeWrapper {
            val response: List<AddressResponseDto> = client.getJson(ADDRESS_ENDPOINT)
            val addressToActivate = response.find { it.id == addressId.toString() }?.toEntity()

            addressToActivate?.let {
                val updatedAddress: AddressResponseDto = client.putJson(
                    requestDto = it.toDto(id = addressId.toString(), isActive = true),
                    path = "$ADDRESS_ENDPOINT/$addressId"
                )
                cacheActiveAddress(updatedAddress)
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
        } ?: throw AddressNotFoundException()
    }

    private suspend fun cacheActiveAddress(address: AddressResponseDto) {
        withContext(dispatcher) {
            try {
                addressDao.deleteActiveAddress()
                addressDao.upsert(address.toLocalEntity())
            } catch (_: Exception) {
            }
        }
    }

    companion object {
        const val ADDRESS_ENDPOINT = "identity/addresses"
        const val ACTIVE_ADDRESS_ENDPOINT = "identity/addresses/active"
        const val DELETE_LOCATION_ENDPOINT = "identity/addresses"
        const val ADDRESS_ID = "id"
    }
}

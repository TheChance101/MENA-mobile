package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import net.thechance.mena.identity.data.utils.deleteJson
import net.thechance.mena.identity.data.utils.getJson
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.repository.AddressRepository

class AddressRepositoryImpl(
    private val client: HttpClient
) : AddressRepository {
    override suspend fun getUserAddresses(): List<Address> = safeWrapper {
        client.getJson(USER_LOCATIONS_ENDPOINT)
    }

    override suspend fun deleteAddress(addressId: Long) = safeWrapper {
        client.deleteJson(
            path = DELETE_LOCATION_ENDPOINT,
        )
    }

    companion object {
        const val USER_LOCATIONS_ENDPOINT = "identity//"
        const val DELETE_LOCATION_ENDPOINT = "identity//"
    }
}


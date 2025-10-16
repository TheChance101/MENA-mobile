package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import net.thechance.mena.identity.data.mapper.toDto
import net.thechance.mena.identity.data.utils.deleteJson
import net.thechance.mena.identity.data.utils.getJson
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.repository.AddressesRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
@OptIn(ExperimentalUuidApi::class)

class AddressesRepositoryImpl(
    val client: HttpClient
) : AddressesRepository {

    override suspend fun createAddress(address: Address) {
        return safeWrapper {
            client.postJson(
                requestDto = address.toDto(),
                path = ADDRESS_ENDPOINT
            )

        }
    }

    override suspend fun editAddress(address: Address) {
        return safeWrapper {
            client.postJson(
                requestDto = address.toDto(),
                path = "$ADDRESS_ENDPOINT/${address.id.toString()}"
            )
        }

    }
    override suspend fun getUserAddresses(): List<Address> = safeWrapper {
        client.getJson(ADDRESS_ENDPOINT)
    }

    override suspend fun deleteAddress(addressId: Uuid) = safeWrapper {
        client.deleteJson(
            path = DELETE_LOCATION_ENDPOINT,
            queryParams = mapOf(ADDRESS_ID to addressId.toString())
        )
    }

    companion object {
        const val ADDRESS_ENDPOINT = "identity/addresses"
        const val DELETE_LOCATION_ENDPOINT = "identity/addresses"
        const val ADDRESS_ID ="id"
    }
}
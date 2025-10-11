package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import net.thechance.mena.identity.data.dto.addresses.AddAddressRequestDto
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.repository.AddressesRepository

class AddressesRepositoryImpl(
    val client: HttpClient
): AddressesRepository {

    override suspend fun createAddress(
        latitude: Double,
        longitude: Double,
        addressLine: String,
        addressType: String,
        otherAddressType: String?
    ) {
        return safeWrapper {
            client.postJson(
                AddAddressRequestDto(
                    latitude,
                    longitude,
                    addressLine,
                    addressType,
                    otherAddressType
                ),
                path = ADDRESS_ENDPOINT
            )

        }
    }

    override suspend fun editAddress(
        id: String,
        latitude: Double,
        longitude: Double,
        addressLine: String,
        addressType: String,
        otherAddressType: String?
    ) {
        return safeWrapper {
           client.postJson(
                AddAddressRequestDto(
                    latitude,
                    longitude,
                    addressLine,
                    addressType,
                    otherAddressType
                ),
                path = "$ADDRESS_ENDPOINT/$id"
           )
        }

    }

    companion object{
        const val ADDRESS_ENDPOINT= "identity/addresses"

    }
}
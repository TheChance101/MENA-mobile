package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import net.thechance.mena.identity.data.mapper.toDto
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.repository.AddressesRepository

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
                path = "$ADDRESS_ENDPOINT/${address.id}"
            )
        }

    }

    companion object {
        const val ADDRESS_ENDPOINT = "identity/addresses"

    }
}
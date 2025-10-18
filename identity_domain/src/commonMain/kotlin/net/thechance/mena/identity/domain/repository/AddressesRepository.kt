package net.thechance.mena.identity.domain.repository

import net.thechance.mena.identity.domain.entity.Address
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface AddressesRepository {
    suspend fun createAddress(address: Address)
    suspend fun editAddress(address: Address)
    suspend fun getUserAddresses(): List<Address>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun deleteAddress(addressId: Uuid)
    suspend fun getActiveAddress(): Address?
}
package net.thechance.mena.identity.domain.repository

import net.thechance.mena.identity.domain.entity.Address
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
@OptIn(ExperimentalUuidApi::class)

interface AddressRepository {
    suspend fun getUserAddresses() : List<Address>
    suspend fun deleteAddress(addressId: Uuid)
}


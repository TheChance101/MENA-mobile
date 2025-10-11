package net.thechance.mena.identity.domain.repository

import net.thechance.mena.identity.domain.entity.Address

interface AddressRepository {
    suspend fun getUserAddresses() : List<Address>
    suspend fun deleteAddress(addressId: Long)
}


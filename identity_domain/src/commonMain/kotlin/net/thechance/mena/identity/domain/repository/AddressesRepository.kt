package net.thechance.mena.identity.domain.repository

import net.thechance.mena.identity.domain.entity.Address

interface AddressesRepository {

    suspend fun createAddress(address: Address)

    suspend fun editAddress(address: Address)
}
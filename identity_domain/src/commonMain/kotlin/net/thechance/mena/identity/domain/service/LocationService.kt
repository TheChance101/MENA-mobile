package net.thechance.mena.identity.domain.service

import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.repository.AddressesRepository

class LocationService(private val addressRepository: AddressesRepository) {

    suspend fun getUserAddresses() =
        addressRepository.getUserAddresses()

    suspend fun getActiveAddress(): Address? {
        return addressRepository.getActiveAddress()
    }
}
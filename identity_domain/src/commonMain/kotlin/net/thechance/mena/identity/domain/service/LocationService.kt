package net.thechance.mena.identity.domain.service

import net.thechance.mena.identity.domain.repository.AddressRepository

class LocationService(private val addressRepository: AddressRepository) {

    suspend fun getUserAddresses() =
        addressRepository.getUserAddresses()
}
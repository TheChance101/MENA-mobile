package net.thechance.mena.identity.domain.service

import kotlinx.coroutines.flow.StateFlow
import net.thechance.mena.identity.domain.repository.AddressRepository
import net.thechance.mena.identity.domain.repository.AuthenticationRepository

class LocationService (private val addressRepository: AddressRepository){

    suspend fun getUserAddresses()=
        addressRepository.getUserAddresses()
}
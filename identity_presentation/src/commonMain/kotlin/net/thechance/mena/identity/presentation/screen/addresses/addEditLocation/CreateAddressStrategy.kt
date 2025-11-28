package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import net.thechance.mena.identity.domain.repository.AddressesRepository
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class CreateAddressStrategy(private val repository: AddressesRepository) :
    AddressOperationStrategy {
    override suspend fun execute(addressData: AddressData) {
        val addressData = addressData as AddressData.New
        repository.createAddress(addressData.input)
    }

}
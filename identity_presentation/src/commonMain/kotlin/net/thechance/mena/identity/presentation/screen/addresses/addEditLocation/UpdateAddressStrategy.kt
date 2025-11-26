package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import net.thechance.mena.identity.domain.repository.AddressesRepository
import kotlin.uuid.ExperimentalUuidApi

class UpdateAddressStrategy(private val repository: AddressesRepository) :
    AddressOperationStrategy {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun execute(addressData: AddressData) {
        val addressData = addressData as AddressData.Existing
        repository.updateAddress(addressData.id, addressData.input, addressData.isMain)
    }

}
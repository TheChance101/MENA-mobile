package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddressOperationStrategyFactory(
    private val createAddress: CreateAddressStrategy,
    private val updateAddress: UpdateAddressStrategy
) {
    @OptIn(ExperimentalUuidApi::class)
    fun getStrategy(addressId: Uuid?): AddressOperationStrategy {
        return if (addressId == null) createAddress else updateAddress
    }
}

package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
interface AddressOperationStrategy {
    suspend fun execute(
        addressData: AddressData
    )
}
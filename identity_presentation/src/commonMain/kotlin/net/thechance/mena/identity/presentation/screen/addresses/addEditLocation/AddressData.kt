package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import net.thechance.mena.identity.domain.model.AddressInput
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
sealed interface AddressData {
    data class New(val input: AddressInput) : AddressData
    data class Existing(val id: Uuid, val input: AddressInput, val isMain: Boolean) : AddressData
}

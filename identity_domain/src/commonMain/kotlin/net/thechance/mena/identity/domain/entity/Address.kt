package net.thechance.mena.identity.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Address @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid? = null,
    val latitude: Double,
    val longitude: Double,
    val addressLine: String,
    val addressType: AddressType
)

sealed class AddressType {
    object Home : AddressType()
    object Office : AddressType()
    data class Other(val addressType: String) : AddressType()

    companion object AddressTypeMapper {
        fun AddressType.getAddressType() =
            when (this) {
                Home -> "Home"
                Office -> "Office"
                is Other -> addressType
            }
        fun getAddressTypeFromString(addressType: String) =
            when (addressType) {
                "Home" -> Home
                "Office" -> Office
                else -> Other(addressType)
            }
    }
}


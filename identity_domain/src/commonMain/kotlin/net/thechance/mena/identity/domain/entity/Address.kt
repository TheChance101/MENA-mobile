package net.thechance.mena.identity.domain.entity
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Address(
    val id:Uuid? = null,
    val latitude: Double,
    val longitude: Double,
    val addressLine: String,
    val addressType: String,
    val otherAddressType: String?,
    val isActive:Boolean
)

enum class AddressType() {
    Home(),
    Office(),
    Other()
}

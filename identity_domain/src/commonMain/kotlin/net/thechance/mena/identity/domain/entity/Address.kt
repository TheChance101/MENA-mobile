package net.thechance.mena.identity.domain.entity

data class Address(
    val id: Long,
    val addressType:AddressType ,
    val isMainAddress:Boolean ,
    val addressDetails:String,
    val longitude: Double ,
    val latitude: Double ,
)

enum class AddressType() {
    HOME(),
    OFFICE(),
    OTHER()
}

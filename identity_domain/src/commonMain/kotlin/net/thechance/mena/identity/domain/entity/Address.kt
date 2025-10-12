package net.thechance.mena.identity.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Address (
    val id: Uuid,
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

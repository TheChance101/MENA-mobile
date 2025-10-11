package net.thechance.mena.identity.domain.repository

interface AddressesRepository {

    suspend fun createAddress(
         latitude: Double,
         longitude: Double,
         addressLine: String,
         addressType: String,
         otherAddressType: String?
    )

    suspend fun editAddress(
         id: String,
         latitude: Double,
         longitude: Double,
         addressLine: String,
         addressType: String,
         otherAddressType: String?,
    )
}
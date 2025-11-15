package net.thechance.mena.dukan.domain.model

data class OrderAddress(
    val addressDeliveryTitle: String,
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double
)

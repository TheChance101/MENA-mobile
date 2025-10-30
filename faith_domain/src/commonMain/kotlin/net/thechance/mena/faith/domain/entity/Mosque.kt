package net.thechance.mena.faith.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Mosque(
    val id: Uuid,
    val name: String,
    val coordinates: Coordinates,
    val address: String,
    val imageUrl: String,
) {
    data class Coordinates(
        val latitude: Double,
        val longitude: Double,
    )
}

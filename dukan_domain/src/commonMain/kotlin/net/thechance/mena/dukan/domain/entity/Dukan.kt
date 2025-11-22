package net.thechance.mena.dukan.domain.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Dukan(
    val id: Uuid,
    val name: String,
    val imageUrl: String,
    val categories: Set<Category>,
    val coordinates: Coordinates,
    val address: String,
    val status: Status,
    val color: Color,
    val style: Style,
    val isFavorite: Boolean
) {
    data class Coordinates(
        val latitude: Double,
        val longitude: Double,
    )

    enum class Style {
        WIDE_IMAGE,
        SMALL_IMAGE,
        NO_IMAGE
    }

    enum class Status {
        PENDING,
        APPROVED,
        REJECTED
    }
    enum class ActivationStatus {
        ACTIVATED,
        DEACTIVATED,
        ONHOLD
    }
}
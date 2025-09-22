package net.thechance.mena.dukan.domain.entity

data class Dukan(
    val id: String,
    val name: String,
    val imageUrl: String,
    val categories: Set<Category>,
    val coordinates:Coordinates,
    val address: String,
    val status: Status,
    val color: Color,
    val style: Style
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
    enum class Status{
        PENDING,
        APPROVED,
        REJECTED
    }
}
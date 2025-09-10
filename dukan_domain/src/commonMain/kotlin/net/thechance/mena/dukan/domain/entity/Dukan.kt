package net.thechance.mena.dukan.domain.entity

data class Dukan(
    val id: String,
    val name: String,
    val imageUrl: String,
    val categories: Set<Category>,
    val location: Location,
    val isPending: Boolean,
    val color: Long,
    val style: Style
) {
    data class Location(
        val latitude: Double,
        val longitude: Double,
        val address: String
    )

    enum class Style {
        WIDE_IMAGE,
        SMALL_IMAGE,
        NO_IMAGE
    }
}
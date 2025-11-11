package net.thechance.mena.admin_panel.domain.entity.dukan

data class Dukan(
    val id: String,
    val name: String,
    val imageUrl: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val color: Color?,
    val style: Style,
    val categories: List<Category>
) {
    enum class Style {
        WIDE_IMAGE,
        SMALL_IMAGE,
        NO_IMAGE,
    }
}
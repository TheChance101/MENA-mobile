package net.thechance.mena.admin_panel.domain.entity.dukan

data class Dukan(
    val id: String,
    val name: String,
    val imageUrl: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val categories: List<Category>
)
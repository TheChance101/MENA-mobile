package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

data class DukanDetailsUiState(
    val name: String = "",
    val imageUrl: String = "",
    val style: Style = Style.NO_IMAGE,
    val color: ColorUiState = ColorUiState(),
    val coordinates: Coordinates = Coordinates(),
    val bestSellingProducts: List<ProductUiState> = emptyList(),
    val shelves: List<ShelfUiState> = emptyList(),
) {
    data class Coordinates(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
    )

    enum class Style {
        WIDE_IMAGE,
        SMALL_IMAGE,
        NO_IMAGE
    }

    data class ColorUiState(
        val id: String = "",
        val color: Long = 0L
    )

    data class ProductUiState(
        val id: String,
        val name: String,
        val imageUrl: String,
        val price: Double,
        val description: String,
        val shelfId: String,
    )

    data class ShelfUiState(
        val id: String,
        val name: String,
        val products: List<ProductUiState>
    )
}
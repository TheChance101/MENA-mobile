package net.thechance.mena.dukan.presentation.component.product.productImage

import androidx.compose.ui.graphics.ImageBitmap

interface ProductImageModel {
    val id: Long
    val image: ImageBitmap
    val imageUrl: String?
    val imageSizeInMegaByte: Double
    val imageState: ProductImageState
    val errorMessage: String?
}


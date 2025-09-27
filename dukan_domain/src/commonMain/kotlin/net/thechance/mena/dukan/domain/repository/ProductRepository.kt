package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Product

interface ProductRepository {

    suspend fun createProduct(product: Product)
    suspend fun getProductsByShelfId(shelfId: String): List<Product>
    suspend fun uploadProductImages(
        fileName: List<String>,
        fileBytes: List<ByteArray>,
        productId: String
    ): List<String>
}
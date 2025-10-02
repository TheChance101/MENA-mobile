package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.util.PagedResult

interface ProductRepository {

    suspend fun createProduct(product: Product,shelfId: String): String
    suspend fun getProductsByShelfId(
        shelfId: String,
        page:Int,
        size:Int
    ): PagedResult<Product>
    suspend fun uploadProductImages(
        fileName: List<String>,
        fileBytes: List<ByteArray>,
        productId: String
    ): List<String>
}
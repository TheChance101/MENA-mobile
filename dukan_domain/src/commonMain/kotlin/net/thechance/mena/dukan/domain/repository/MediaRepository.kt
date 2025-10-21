package net.thechance.mena.dukan.domain.repository

interface MediaRepository {
    suspend fun uploadDukanImage(fileName: String, fileBytes: ByteArray): String
    suspend fun uploadProductImages(
        fileName: List<String>,
        fileBytes: List<ByteArray>,
        productId: String
    ): List<String>

}
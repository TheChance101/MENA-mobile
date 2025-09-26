package net.thechance.mena.wallet.presentation.utils

expect class ImageSharer {
    suspend fun shareImage(imageBytes: ByteArray, fileName: String, mimeType: String)
    suspend fun saveImageToGallery(imageBytes: ByteArray, fileName: String): Boolean
}

expect fun getImageSharer(): ImageSharer
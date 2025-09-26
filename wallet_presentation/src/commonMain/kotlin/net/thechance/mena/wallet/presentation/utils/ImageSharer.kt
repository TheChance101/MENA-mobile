package net.thechance.mena.wallet.presentation.utils

expect class ImageSharer {
    suspend fun shareImage(imageBytes: ByteArray, fileName: String, mimeType: String)
}

expect fun getImageSharer(): ImageSharer
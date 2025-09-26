package net.thechance.mena.wallet.presentation.utils

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class ImageSharer {
    suspend fun shareImage(imageBytes: ByteArray, fileName: String, mimeType: String)
}

expect fun getImageSharer(): ImageSharer
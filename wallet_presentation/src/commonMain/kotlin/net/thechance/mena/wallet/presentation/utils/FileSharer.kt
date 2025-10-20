package net.thechance.mena.wallet.presentation.utils

interface FileSharer {
    suspend fun shareFile(fileBytes: ByteArray, fileName: String, mimeType: String, shareTitle: String)
}

expect class FileSharerImpl : FileSharer {
    override suspend fun shareFile(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        shareTitle: String
    )
}
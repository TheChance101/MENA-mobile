package net.thechance.mena.wallet.presentation.utils

import org.koin.core.annotation.Single

interface FileSharer {
    suspend fun shareFile(fileBytes: ByteArray, fileName: String, mimeType: String, shareTitle: String)
}

@Single
expect class FileSharerImpl(fileManager: FileManager) : FileSharer {
    override suspend fun shareFile(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        shareTitle: String
    )
}
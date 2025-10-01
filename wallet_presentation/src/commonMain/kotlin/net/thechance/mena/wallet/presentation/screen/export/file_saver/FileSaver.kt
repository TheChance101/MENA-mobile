package net.thechance.mena.wallet.presentation.screen.export.file_saver

interface FileSaver {
    suspend fun saveFile(suggestedName: String, extension: String, bytes: ByteArray): Boolean
}


package net.thechance.mena.wallet.presentation.utils

interface PdfHandler {
    suspend fun splitToPagesOfPngs(pdfData: ByteArray): List<ByteArray>
    suspend fun sharePdf(pdfData: ByteArray, fileName: String)
    suspend fun saveStatement(byteArray: ByteArray, location: StorageLocation): String
    suspend fun deleteStatement(location: StorageLocation)
    suspend fun getPdfBytes(location: StorageLocation): ByteArray
    suspend fun checkIfStatementExists(location: StorageLocation): Boolean
}

expect fun getPdfHandler(): PdfHandler

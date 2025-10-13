package net.thechance.mena.wallet.presentation.utils

interface PdfHandler {
    suspend fun splitToPagesOfPngs(pdfData: ByteArray): List<ByteArray>
    suspend fun sharePdf(pdfData: ByteArray, fileName: String)
    suspend fun downloadPdf(pdfData: ByteArray, fileName: String): String
    suspend fun saveToCache(pdfData: ByteArray, fileName: String): String
    suspend fun deleteStatement(location: StorageLocation)
    suspend fun getPdfBytes(location: StorageLocation): ByteArray
    suspend fun checkIfStatementExists(location: StorageLocation): Boolean
}

expect fun getPdfHandler(): PdfHandler

package net.thechance.mena.wallet.presentation.utils

interface PdfHandler {
    suspend fun splitToPagesOfPngs(pdfData: ByteArray): List<ByteArray>
    suspend fun sharePdf(pdfData: ByteArray, fileName: String)
    suspend fun downloadPdf(pdfData: ByteArray, fileName: String): String
}

expect fun getPdfHandler(): PdfHandler

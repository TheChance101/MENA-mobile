package net.thechance.mena.wallet.presentation.utils

interface PdfShare {
    suspend fun sharePdf(
        pdfBytes: ByteArray,
        fileName: String,
        mimeType: String = "application/pdf"
    )
}

expect fun getPdfShare(): PdfShare
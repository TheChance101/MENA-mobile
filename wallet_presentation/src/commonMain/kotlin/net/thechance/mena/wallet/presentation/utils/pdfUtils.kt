package net.thechance.mena.wallet.presentation.utils

import org.koin.core.annotation.Single

@Single
expect class PdfHandler() {
    suspend fun splitToPagesOfPngs(pdfData: ByteArray): List<ByteArray>
    suspend fun sharePdf(pdfData: ByteArray, fileName: String)
}
package net.thechance.mena.wallet.presentation.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Provided
import org.koin.core.context.GlobalContext
import java.io.File

class PdfShareImpl(@Provided private val context: Context) : PdfShare {
    override suspend fun sharePdf(
        pdfBytes: ByteArray,
        fileName: String,
        mimeType: String
    ) {
        val contentUri = withContext(Dispatchers.IO) {
            val file = File(context.cacheDir, fileName)
            file.writeBytes(pdfBytes)
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, "Share PDF")
            putExtra(Intent.EXTRA_TEXT, "Sharing PDF file")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "Share PDF")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooserIntent)
    }
}

actual fun getPdfShare(): PdfShare {
    val context = GlobalContext.get().get<Context>()
    return PdfShareImpl(context)
}
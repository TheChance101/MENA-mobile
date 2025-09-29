package net.thechance.mena.wallet.presentation.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Provided
import org.koin.core.context.GlobalContext
import java.io.File

class ImageSharerImpl (@Provided private val context: Context) : ImageSharer {
    override suspend fun shareImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ) {
        val contentUri = withContext(Dispatchers.IO) {
            val file = File(context.cacheDir, fileName)
            file.writeBytes(imageBytes)
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "Share Image")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooserIntent)
    }
}

actual fun getImageSharer(): ImageSharer {
    val context = GlobalContext.get().get<Context>()
    return ImageSharerImpl(context)
}
package net.thechance.mena.wallet.presentation.utils

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single
import org.koin.core.context.GlobalContext
import java.io.File

@Single
actual class FileSharerImpl actual constructor(@Provided private val fileManager: FileManager) : FileSharer {
    private val context = GlobalContext.get().get<Context>()

    actual override suspend fun shareFile(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        shareTitle: String
    ) {
        val cachedFilePath = fileManager.saveFile(
            fileBytes,
            StorageLocation.Cache(fileName),
            mimeType
        )

        val contentUri = withContext(Dispatchers.IO) {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                File(cachedFilePath)
            )
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_TITLE, shareTitle)
            clipData = ClipData.newRawUri(null, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(shareIntent, shareTitle)
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooserIntent)
    }
}
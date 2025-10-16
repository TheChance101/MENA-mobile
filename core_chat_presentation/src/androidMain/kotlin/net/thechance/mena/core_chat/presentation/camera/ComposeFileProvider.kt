package net.thechance.mena.core_chat.presentation.camera

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import net.thechance.mena.core_chat.presentation.R
import java.io.File
import java.util.Objects

class ComposeFileProvider: FileProvider(R.xml.path_provider) {
    companion object {
        fun getImageUri(context: Context): Uri? = runCatching {
            val tempFile = File.createTempFile(
                "picture_${System.currentTimeMillis()}", ".jpeg", context.cacheDir
            ).apply {
                createNewFile()
            }
            val authority = context.applicationContext.packageName + ".fileprovider"
            getUriForFile(
                Objects.requireNonNull(context),
                authority,
                tempFile,
            )
        }.getOrNull()
    }
}
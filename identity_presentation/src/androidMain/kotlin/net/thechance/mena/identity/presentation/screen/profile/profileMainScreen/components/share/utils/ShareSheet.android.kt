package net.thechance.mena.identity.presentation.screen.profile.profileMainScreen.components.share.utils

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.thechance.mena.identity.presentation.R
import java.io.File

@Composable
actual fun ShareSheet(title: String, message: String, shareLink: String, onDismiss: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(message, title) {
        val contentPreviewUri = getAndCacheImageFile(context = context).toImageUri(context)

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "$message\n\n$shareLink")
            putExtra(Intent.EXTRA_TITLE, title)

            clipData = ClipData.newRawUri(title, contentPreviewUri)

            setDataAndType(contentPreviewUri, "text/plain")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val shareIntent = Intent.createChooser(sendIntent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareIntent)
        onDismiss()
    }
}

private suspend fun getAndCacheImageFile(context: Context): File {
    return withContext(Dispatchers.IO) {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()

        val file = File(cachePath, "mena_logo.png")

        AppCompatResources.getDrawable(context, R.drawable.mena_logo)?.toBitmap()?.let { bitmap ->
            file.outputStream().use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
        }
        file
    }
}

private fun File.toImageUri(context: Context): Uri {
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        this
    )
}
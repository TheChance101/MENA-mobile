package net.thechance.mena.faith.presentation.util

import android.content.ClipData
import android.content.Context

actual open class ClipboardManager(
    private val context: Context
) {
   actual open fun copy(text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE)
                    as android.content.ClipboardManager
        val clipData = ClipData.newPlainText("Ayah Text", text)
        clipboardManager.setPrimaryClip(clipData)
    }
}
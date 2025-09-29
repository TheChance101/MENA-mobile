package net.thechance.mena.faith.presentation.util

import android.content.ClipData
import android.content.Context

actual class ClipboardManagerImp(
    private val context: Context
) : ClipboardManager {
    actual override fun copy(text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE)
                    as android.content.ClipboardManager
        val clipData = ClipData.newPlainText("Ayah Text", text)
        clipboardManager.setPrimaryClip(clipData)
    }
}
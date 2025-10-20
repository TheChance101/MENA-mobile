package net.thechance.mena.faith.presentation.utils

import android.content.ClipData
import android.content.Context

actual class ClipboardManagerImpl(
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
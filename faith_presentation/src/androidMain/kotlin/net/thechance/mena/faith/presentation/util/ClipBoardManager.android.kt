package net.thechance.mena.faith.presentation.util

import android.content.ClipData
import android.content.Context

private var applicationContext: Context? = null

fun initializeClipboardContext(context: Context) {
    applicationContext = context.applicationContext
}

actual fun getClipboardManager(): ClipboardManager {
    return AndroidClipboardManager()
}

class AndroidClipboardManager : ClipboardManager {

    override fun copy(text: String) {
        val context = applicationContext
        requireNotNull(context)

        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clipData = ClipData.newPlainText("Ayah Text", text)
        clipboardManager.setPrimaryClip(clipData)
    }
}
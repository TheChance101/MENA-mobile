package net.thechance.mena.faith.presentation.util

import platform.UIKit.UIPasteboard

actual fun getClipboardManager(): ClipboardManager {
    return IOSClipboardManager()
}

class IOSClipboardManager : ClipboardManager {

    override fun copy(text: String) {
        val pasteboard = UIPasteboard.generalPasteboard
        pasteboard.string = text
    }
}
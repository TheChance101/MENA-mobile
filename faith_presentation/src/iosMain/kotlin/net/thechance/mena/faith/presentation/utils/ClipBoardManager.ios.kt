package net.thechance.mena.faith.presentation.utils

import platform.UIKit.UIPasteboard

actual class ClipboardManagerImpl : ClipboardManager {
    actual override fun copy(text: String) {
        val pasteboard = UIPasteboard.generalPasteboard
        pasteboard.string = text
    }
}
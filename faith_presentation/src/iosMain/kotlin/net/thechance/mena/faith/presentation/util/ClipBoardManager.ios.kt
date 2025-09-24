package net.thechance.mena.faith.presentation.util

import platform.UIKit.UIPasteboard

actual open class ClipboardManager {
    actual open fun copy(text: String) {
        val pasteboard = UIPasteboard.generalPasteboard
        pasteboard.string = text
    }
}
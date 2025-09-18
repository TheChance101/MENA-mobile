package net.thechance.mena.faith.presentation.util

import platform.UIKit.UIPasteboard

actual class ClipboardManager {
    actual fun copy(text: String) {
        val pasteboard = UIPasteboard.generalPasteboard
        pasteboard.string = text
    }
}
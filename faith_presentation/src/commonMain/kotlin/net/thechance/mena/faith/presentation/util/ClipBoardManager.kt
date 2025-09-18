package net.thechance.mena.faith.presentation.util

interface ClipboardManager {
    fun copy(text: String)
}

expect fun getClipboardManager(): ClipboardManager

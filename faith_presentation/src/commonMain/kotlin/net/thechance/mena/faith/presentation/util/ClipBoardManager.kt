package net.thechance.mena.faith.presentation.util

expect class ClipboardManagerImp : ClipboardManager {
    override fun copy(text: String)
}

interface ClipboardManager {
    fun copy(text: String)
}
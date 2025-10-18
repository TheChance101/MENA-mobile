package net.thechance.mena.core_chat.presentation.camera

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

@OptIn(ExperimentalForeignApi::class)
fun UIImage?.toByteArray(): ByteArray? {
    return if (this != null) {
        var quality = 1.0
        var imageData = UIImageJPEGRepresentation(this, quality) ?: return null

        while (imageData.length.toLong() > 5_000_000 && quality > 0.1) {
            quality -= 0.1
            imageData = UIImageJPEGRepresentation(this, quality) ?: break
        }

        val bytes = imageData.bytes ?: throw IllegalArgumentException("image bytes is null")
        val length = imageData.length
        val data: CPointer<ByteVar> = bytes.reinterpret()
        ByteArray(length.toInt()) { index -> data[index] }
    } else {
        null
    }
}

package net.thechance.mena.core_chat.data.utils

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders


fun Pair<String, ByteArray>.buildImageMultiPartFormData(
    fieldName: String,
    messageId: String,
    chatId: String,
): MultiPartFormDataContent {
    val (name, byteArray) = this
    val extension = byteArray.getExtension()
    val formattedFileName = formatFileName(name, extension)

    return MultiPartFormDataContent(
        formData {
            append("messageId", messageId)
            append("chatId", chatId)
            append(
                fieldName,
                byteArray,
                Headers.build {
                    append(HttpHeaders.ContentType, imageExtensionToMimeType(extension))
                    append(HttpHeaders.ContentDisposition, """filename="$formattedFileName"""")
                }
            )
        }
    )
}

private fun ByteArray.getExtension(): String = when {
    isEmpty() -> "octet-stream"
    size >= 8 &&
            get(0) == 0x89.toByte() &&
            get(1) == 0x50.toByte() &&
            get(2) == 0x4E.toByte() &&
            get(3) == 0x47.toByte() &&
            get(4) == 0x0D.toByte() &&
            get(5) == 0x0A.toByte() &&
            get(6) == 0x1A.toByte() &&
            get(7) == 0x0A.toByte() -> "png"

    size >= 3 &&
            get(0) == 0xFF.toByte() &&
            get(1) == 0xD8.toByte() -> "jpg"

    size >= 12 &&
            copyOfRange(0, 4).contentEquals(byteArrayOf(0x52,0x49,0x46,0x46)) &&
            copyOfRange(8, 12).contentEquals(byteArrayOf(0x57,0x45,0x42,0x50))
        -> "webp"

    else -> "bin"
}

private fun imageExtensionToMimeType(extension: String): String = when (extension.lowercase()) {
    "png" -> "image/png"
    "jpg"-> "image/jpeg"
    "webp" -> "image/webp"
    else -> "application/octet-stream"
}

private fun formatFileName(name: String, extension: String): String {
    val nameWithExtension = if (name.contains('.')) name else "$name.${extension.ifBlank { "bin" }}"
    return nameWithExtension
        .replace("\"", "")
        .replace("\r", "")
        .replace("\n", "")
        .replace(";", "_")
        .replace("+", "_")
        .take(150)
}

package net.thechance.mena.core_chat.data.utils

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

fun Pair<String, ByteArray>.buildAudioMultiPartFormData(
    fieldName: String,
    messageId: String,
    chatId: String,
    audioDurationMs: Long? = null
): MultiPartFormDataContent {
    val (name, byteArray) = this
    val extension = byteArray.getAudioExtension()
    val formattedFileName = formatFileName(name, extension)

    return MultiPartFormDataContent(
        formData {
            append("messageId", messageId)
            append("chatId", chatId)
            audioDurationMs?.let { append("audioDurationMs", it.toString()) }
            append(
                fieldName,
                byteArray,
                Headers.build {
                    append(HttpHeaders.ContentType, audioExtensionToMimeType(extension))
                    append(HttpHeaders.ContentDisposition, """filename="$formattedFileName"""")
                }
            )
        }
    )
}

private fun ByteArray.getAudioExtension(): String = when {
    size >= 8 &&
            get(4) == 0x66.toByte() &&
            get(5) == 0x74.toByte() &&
            get(6) == 0x79.toByte() &&
            get(7) == 0x70.toByte() -> "m4a"

    size >= 2 &&
            get(0) == 0xFF.toByte() &&
            (get(1) == 0xFB.toByte() || get(1) == 0xF3.toByte()) -> "mp3"

    size >= 12 &&
            this[0] == 0x52.toByte() &&
            this[1] == 0x49.toByte() &&
            this[2] == 0x46.toByte() &&
            this[3] == 0x46.toByte() &&
            this[8] == 0x57.toByte() &&
            this[9] == 0x41.toByte() &&
            this[10] == 0x56.toByte() &&
            this[11] == 0x45.toByte()
        -> "wav"

    else -> "m4a"
}

private fun audioExtensionToMimeType(extension: String): String = when (extension.lowercase()) {
    "m4a" -> "audio/m4a"
    "mp3" -> "audio/mpeg"
    "aac" -> "audio/aac"
    "wav" -> "audio/wav"
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
package net.thechance.mena.dukan.data.util.network

import io.ktor.client.request.forms.*
import io.ktor.http.*

fun buildSinglePartFormData(
    fileName: String,
    fileBytes: ByteArray,
    key: String
): MultiPartFormDataContent {
    return MultiPartFormDataContent(
        formData {
            val ext = sniffExt(fileBytes, fileName)
            val safeName = sanitizeFileName(ensureExt(fileName, ext))
            append(
                key = key,
                value = fileBytes,
                headers = Headers.build {
                    append(HttpHeaders.ContentType, mimeForExt(ext))
                    append(HttpHeaders.ContentDisposition, """filename="$safeName"""")
                }
            )
        }
    )
}


fun buildMultiPartFormData(
    files: List<Pair<String, ByteArray>>,
    fieldName: String = "files"
): MultiPartFormDataContent {
    return MultiPartFormDataContent(
        formData {
            files.forEach { (originalName, bytes) ->
                val ext = sniffExt(bytes, originalName)
                val safeName = sanitizeFileName(ensureExt(originalName, ext))

                append(
                    fieldName,
                    bytes,
                    Headers.build {
                        append(HttpHeaders.ContentType, mimeForExt(ext))
                        append(HttpHeaders.ContentDisposition, """filename="$safeName"""")
                    }
                )
            }
        }
    )
}


private fun sniffExt(bytes: ByteArray, originalName: String): String {
    return when {
        bytes.size >= 8 &&
                bytes[0] == 0x89.toByte() &&
                bytes[1] == 0x50.toByte() &&
                bytes[2] == 0x4E.toByte() &&
                bytes[3] == 0x47.toByte() &&
                bytes[4] == 0x0D.toByte() &&
                bytes[5] == 0x0A.toByte() &&
                bytes[6] == 0x1A.toByte() &&
                bytes[7] == 0x0A.toByte() -> "png"

        bytes.size >= 3 &&
                bytes[0] == 0xFF.toByte() &&
                bytes[1] == 0xD8.toByte() -> "jpg"

        bytes.size >= 12 &&
                bytes.copyOfRange(0, 4).contentEquals(byteArrayOf(0x52,0x49,0x46,0x46)) &&
                bytes.copyOfRange(8, 12).contentEquals(byteArrayOf(0x57,0x45,0x42,0x50))
            -> "webp"

        originalName.contains('.') -> originalName.substringAfterLast('.').lowercase()
        else -> "bin"
    }
}

private fun mimeForExt(ext: String) = when (ext.lowercase()) {
    "png" -> "image/png"
    "jpg", "jpeg" -> "image/jpeg"
    "webp" -> "image/webp"
    else -> "application/octet-stream"
}

private fun ensureExt(name: String, ext: String): String =
    if (name.contains('.')) name else "$name.${ext.ifBlank { "bin" }}"

private fun sanitizeFileName(name: String): String =
    name.replace("\"", "")
        .replace("\r", "")
        .replace("\n", "")
        .replace(";", "_")
        .replace("+", "_")
        .take(150)
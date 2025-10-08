package net.thechance.mena.trends.data.util

fun getMediaMimeType(fileName: String): String {
    val extension = fileName.substringAfterLast(".", "")
    return mimeTypes[extension] ?: "application/octet-stream"
}

private val mimeTypes = mapOf(
    "mp4" to "video/mp4",
    "mov" to "video/quicktime",
    "mkv" to "video/x-matroska"
)
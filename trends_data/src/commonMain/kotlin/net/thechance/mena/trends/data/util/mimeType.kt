package net.thechance.mena.trends.data.util

enum class MimeType(val value: String) {
    MP4("video/mp4"),
    MOV("video/quicktime"),
    MKV("video/x-matroska"),
    WEBM("video/webm"),
    DEFAULT("application/octet-stream")
}

fun getMediaMimeType(fileName: String): String {
    val extension = fileName.substringAfterLast(".", "")
    return when(extension) {
        "mp4" -> MimeType.MP4.value
        "mov" -> MimeType.MOV.value
        "mkv" -> MimeType.MKV.value
        "webm" -> MimeType.WEBM.value
        else -> MimeType.DEFAULT.value
    }
}
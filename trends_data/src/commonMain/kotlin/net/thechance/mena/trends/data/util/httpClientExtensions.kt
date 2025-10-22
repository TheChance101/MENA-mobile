package net.thechance.mena.trends.data.util

import io.ktor.client.plugins.onUpload
import io.ktor.client.request.HttpRequestBuilder

fun HttpRequestBuilder.observeUploading(
    onProgress: suspend (sent: Long, total: Long) -> Unit
) {
    onUpload { bytesSentTotal, contentLength ->
        if (contentLength != null && contentLength > 0) {
            onProgress(bytesSentTotal, contentLength)
        }
    }
}
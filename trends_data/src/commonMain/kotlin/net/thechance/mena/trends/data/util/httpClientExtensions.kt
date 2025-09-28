package net.thechance.mena.trends.data.util

import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.onUpload
import io.ktor.client.plugins.timeout
import io.ktor.client.request.HttpRequestBuilder

fun HttpRequestBuilder.infiniteTimeOut() {
    timeout {
        connectTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
        requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
        socketTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
    }
}

fun HttpRequestBuilder.observeUploading(
    onProgress: suspend (sent: Long, total: Long) -> Unit
) {
    onUpload { bytesSentTotal, contentLength ->
        if (contentLength != null && contentLength > 0) {
            onProgress(bytesSentTotal, contentLength)
        }
    }
}
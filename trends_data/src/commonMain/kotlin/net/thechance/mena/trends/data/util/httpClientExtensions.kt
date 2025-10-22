package net.thechance.mena.trends.data.util

import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.onUpload
import io.ktor.client.plugins.timeout
import io.ktor.client.request.HttpRequestBuilder
import net.thechance.mena.trends.domain.model.UploadReelProgress

fun HttpRequestBuilder.setUploadRequestTimeout() {
    timeout {
        requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
        socketTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
    }
}

fun HttpRequestBuilder.observeUploading(
    onProgress: suspend (UploadReelProgress) -> Unit
) {
    onUpload { bytesSentTotal, contentLength ->
        if (contentLength != null && contentLength > 0) {
            onProgress(
                UploadReelProgress(
                    numberOfUploadedBytes = bytesSentTotal,
                    totalBytes = contentLength
                )
            )
        }
    }
}
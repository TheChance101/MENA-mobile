package net.thechance.mena.trends.data.util

import io.ktor.client.plugins.onUpload
import io.ktor.client.request.HttpRequestBuilder
import net.thechance.mena.trends.domain.model.UploadReelProgress

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
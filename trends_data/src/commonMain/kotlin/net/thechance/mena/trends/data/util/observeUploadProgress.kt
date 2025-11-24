package net.thechance.mena.trends.data.util

import io.ktor.client.plugins.onUpload
import io.ktor.client.request.HttpRequestBuilder
import net.thechance.mena.trends.domain.model.UploadTrendProgress

fun HttpRequestBuilder.observeUploading(
    totalSize: Long,
    onProgress: suspend (UploadTrendProgress) -> Unit
) {
    onUpload { bytesSentTotal, _ ->
        onProgress(
            UploadTrendProgress(
                numberOfUploadedBytes = bytesSentTotal,
                totalBytes = totalSize
            )
        )
    }
}
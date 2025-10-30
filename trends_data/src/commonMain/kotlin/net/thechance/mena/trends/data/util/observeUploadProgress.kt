package net.thechance.mena.trends.data.util

import io.ktor.client.plugins.onUpload
import io.ktor.client.request.HttpRequestBuilder
import net.thechance.mena.trends.domain.model.UploadReelProgress

fun HttpRequestBuilder.observeUploading(
    totalSize: Long,
    onProgress: suspend (UploadReelProgress) -> Unit
) {
    onUpload { bytesSentTotal, _ ->
        onProgress(
            UploadReelProgress(
                numberOfUploadedBytes = bytesSentTotal,
                totalBytes = totalSize
            )
        )
    }
}
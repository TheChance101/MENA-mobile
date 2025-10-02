package net.thechance.mena.dukan.data.repository.util

import io.ktor.client.request.forms.*
import io.ktor.http.*

// For single file (Dukan)
fun buildSinglePartFormData(
    fileName: String,
    fileBytes: ByteArray,
    key: String
): MultiPartFormDataContent {
    return MultiPartFormDataContent(
        formData {
            append(
                key = key,
                value = fileBytes,
                headers = Headers.build {
                    append(HttpHeaders.ContentType, "multipart/form-data")
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                }
            )
        }
    )
}

// For multiple files (Products)
fun buildMultiPartFormData(
    fileNames: List<String>,
    fileBytes: List<ByteArray>,
    key: String
): MultiPartFormDataContent {
    return MultiPartFormDataContent(
        formData {
            fileNames.zip(fileBytes).forEach { (fileName, bytes) ->
                append(
                    key = key,
                    value = bytes,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, "multipart/form-data")
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    }
                )
            }
        }
    )
}
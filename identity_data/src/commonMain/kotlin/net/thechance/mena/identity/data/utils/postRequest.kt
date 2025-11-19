package net.thechance.mena.identity.data.utils

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

suspend inline fun <reified T, reified R> HttpClient.postJson(
    requestDto: T,
    path: String,
): R {
    val response = this.post {
        url(path)
        contentType(ContentType.Application.Json)
        setBody(requestDto)
    }

    if (response.status != HttpStatusCode.OK) {
        throw ClientRequestException(response, response.body())
    }

    return response.body()
}

suspend fun HttpClient.postEmpty(
    path: String,
) {
    val response = this.post {
        url(path)
    }

    if (response.status != HttpStatusCode.OK) {
        throw ClientRequestException(response, response.body())
    }
}

suspend fun HttpClient.postFileWithData(
    path: String,
    fileKey: String,
    imageByteArray: ByteArray?
) {
    if (imageByteArray == null) return

    val response = submitFormWithBinaryData(
        url = path,
        formData = formData {
            append(
                key = fileKey,
                value = imageByteArray,
                headers = Headers.build {
                    append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
                    append(HttpHeaders.ContentDisposition, "filename=image.jpeg")
                }
            )
        }
    )

    if (response.status != HttpStatusCode.OK) {
        throw ClientRequestException(response, response.body())
    }
}

suspend fun HttpClient.postFileWithDataAndTokens(
    path: String,
    fileKey: String,
    imageByteArray: ByteArray?,
    accessToken: String
) {
    if (imageByteArray == null) return

    val response = this.post {
        url(path)
        header(HttpHeaders.Authorization, "Bearer $accessToken")
        setBody(
            MultiPartFormDataContent(
                formData {
                    append(
                        key = fileKey,
                        value = imageByteArray,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
                            append(HttpHeaders.ContentDisposition, "filename=image.jpeg")
                        }
                    )
                }
            )
        )
    }

    if (response.status != HttpStatusCode.OK) {
        throw ClientRequestException(response, response.body())
    }
}
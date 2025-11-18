package net.thechance.mena.identity.data.utils

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

internal suspend inline fun <reified R> HttpClient.getJson(
    path: String,
    queryParams: Map<String, String> = emptyMap(),
    headerParams: Map<String, String> = emptyMap(),
): R {
    val response = this.get {
        url(path)

        queryParams.forEach { query ->
            url.parameters.append(query.key, query.value)
        }

        headerParams.forEach { header ->
            headers.append(header.key, header.value)
        }

        contentType(ContentType.Application.Json)

    }

    if (response.status != HttpStatusCode.OK) {
        throw ClientRequestException(response, response.body())
    }

    return response.body()
}

internal suspend inline fun <reified T, reified R> HttpClient.getJsonWithBody(
    requestDto: T,
    path: String,
): R {
    val response = this.request {
        method = HttpMethod.Get
        url(path)
        contentType(ContentType.Application.Json)
        setBody(requestDto)
    }

    if (response.status != HttpStatusCode.OK) {
        throw ClientRequestException(response, response.body())
    }

    return response.body()
}
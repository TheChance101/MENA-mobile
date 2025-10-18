package net.thechance.mena.identity.data.utils

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.delete
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

internal suspend fun HttpClient.deleteJson(
    path: String,
    queryParams: Map<String, String> = emptyMap(),
){
    val response = this.delete {
        url(path)
        queryParams.forEach { query ->
            url.parameters.append(query.key, query.value)
        }

        contentType(ContentType.Application.Json)
    }

    if (response.status != HttpStatusCode.OK) {
        throw ClientRequestException(response, response.body())
    }
}
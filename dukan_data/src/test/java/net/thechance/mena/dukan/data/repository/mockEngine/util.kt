package net.thechance.mena.dukan.data.repository.mockEngine

import io.ktor.client.HttpClient
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.serialization.json.Json
import net.thechance.mena.dukan.data.util.network.DukanApi


val jsonSerialization = Json { ignoreUnknownKeys = true }
val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

class MockDukanApiClient(private val httpClient: HttpClient) : DukanApi {
    override fun getClient(): HttpClient = httpClient
    override fun reset() {}
}
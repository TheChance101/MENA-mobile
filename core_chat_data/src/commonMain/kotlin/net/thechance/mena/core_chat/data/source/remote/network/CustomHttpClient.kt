package net.thechance.mena.core_chat.data.source.remote.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.domain.service.AuthorizationService


class CustomHttpClient(
    private val baseUrl: String,
    private val authorizationService: AuthorizationService,
    private val httpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>,
    private val json: Json
) {
    private var client: HttpClient = buildClient()

    private fun buildClient(): HttpClient {
        return createHttpClient(
            baseUrl = baseUrl,
            authorizationService = authorizationService,
            httpClientEngineFactory = httpClientEngineFactory,
            json = json
        )
    }

    fun getClient(): HttpClient{
        return client
    }

    fun reset(){
        client.close()
        client = buildClient()
    }

}
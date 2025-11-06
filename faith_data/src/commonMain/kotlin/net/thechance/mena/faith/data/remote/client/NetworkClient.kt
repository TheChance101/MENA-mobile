package net.thechance.mena.faith.data.remote.client

import io.ktor.client.HttpClient
import net.thechance.mena.identity.domain.service.AuthorizationService

class NetworkClient(
    private val authorizationService: AuthorizationService,
    private val baseUrl: String
) {
    fun provideHttpClient(): HttpClient = configureBaseSettings()

    private fun configureBaseSettings(): HttpClient {
        return provideHttpClint(authorizationService, baseUrl)
    }
}

expect fun provideHttpClint(
    authorizationService: AuthorizationService,
    baseUrl: String,
): HttpClient
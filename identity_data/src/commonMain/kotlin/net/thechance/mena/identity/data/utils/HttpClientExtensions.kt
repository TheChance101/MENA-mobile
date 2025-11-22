package net.thechance.mena.identity.data.utils

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider

fun HttpClient.invalidateAuthTokens() {
    try {
        authProvider<BearerAuthProvider>()?.clearToken()
    } catch (_: Throwable) {
    }
}
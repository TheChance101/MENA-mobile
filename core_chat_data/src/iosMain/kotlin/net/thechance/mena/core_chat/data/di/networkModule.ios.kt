package net.thechance.mena.core_chat.data.di

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual fun createHttpClientEngine(): HttpClientEngineFactory<HttpClientEngineConfig> {
    return Darwin
}
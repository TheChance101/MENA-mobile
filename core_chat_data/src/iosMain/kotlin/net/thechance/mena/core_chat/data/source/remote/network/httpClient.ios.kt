package net.thechance.mena.core_chat.data.source.remote.network

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual val httpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig> = Darwin
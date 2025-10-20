package net.thechance.mena.dukan.data.util.network

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

actual val platformHttpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>
    get() = CIO
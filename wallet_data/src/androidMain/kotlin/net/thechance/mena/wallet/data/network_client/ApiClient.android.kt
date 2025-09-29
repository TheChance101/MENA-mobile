package net.thechance.mena.wallet.data.network_client

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

actual val platformHttpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig>
    get() = OkHttp
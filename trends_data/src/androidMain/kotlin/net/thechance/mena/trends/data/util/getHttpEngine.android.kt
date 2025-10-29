package net.thechance.mena.trends.data.util

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun getHttpEngine(): HttpClientEngine = OkHttp.create()
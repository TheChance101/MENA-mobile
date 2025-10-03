package net.thechance.mena.trends.data.util

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun getHttpEngine(): HttpClientEngine = Darwin.create()
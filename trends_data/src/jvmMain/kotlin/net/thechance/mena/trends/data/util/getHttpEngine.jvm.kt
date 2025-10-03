package net.thechance.mena.trends.data.util

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO

actual fun getHttpEngine(): HttpClientEngine = CIO.create()
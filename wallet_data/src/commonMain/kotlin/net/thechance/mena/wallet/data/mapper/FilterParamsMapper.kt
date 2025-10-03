package net.thechance.mena.wallet.data.mapper

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import net.thechance.mena.wallet.domain.model.TransactionFilterParams

fun TransactionFilterParams.toRequest(): HttpRequestBuilder.() -> Unit = {
    types?.forEach { parameter("type", it.name) }
    status?.let { parameter("status", it.name) }
    startDate?.let { parameter("startDate", it.toString()) }
    endDate?.let { parameter("endDate", it.toString()) }
}
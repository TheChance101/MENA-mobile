package net.thechance.mena.wallet.data.mapper

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import net.thechance.mena.wallet.domain.model.TransactionFilterParams

fun TransactionFilterParams.toStatementRequest(): HttpRequestBuilder.() -> Unit = {
    header(HttpHeaders.Accept, ContentType.Application.Pdf)
    types?.let { parameter("type", it.joinToString(",")) }
    startDate?.let { parameter("startDate", it.toString()) }
    endDate?.let { parameter("endDate", it.toString()) }
}
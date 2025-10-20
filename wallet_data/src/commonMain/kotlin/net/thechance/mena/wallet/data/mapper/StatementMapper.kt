package net.thechance.mena.wallet.data.mapper

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import net.thechance.mena.wallet.data.dto.local.LocalStatement
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.model.StatementWithMetaData
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import kotlin.uuid.ExperimentalUuidApi

fun TransactionFilterParams.toStatementRequest(): HttpRequestBuilder.() -> Unit = {
    header(HttpHeaders.Accept, ContentType.Application.Pdf)
    types?.let { parameter("type", it.joinToString(",")) }
    startDate?.let { parameter("startDate", it.toString()) }
    endDate?.let { parameter("endDate", it.toString()) }
}

@OptIn(ExperimentalUuidApi::class)
fun Statement.toLocal(): LocalStatement {
    return LocalStatement(
        startDate = startDate.toString(),
        endDate = endDate.toString(),
        totalInflows = totalInflows,
        totalOutflows = totalOutflows,
        fileName = fileName
    )
}

fun List<LocalStatement>.toStatementEntityList(): List<Statement> {
    return this.map { it.toEntity() }
}

@OptIn(ExperimentalUuidApi::class)
fun LocalStatement.toEntity(): Statement {
    return Statement(
        startDate = parseLocalDateOrDefault(this.startDate),
        endDate = parseLocalDateOrDefault(this.endDate),
        totalInflows = totalInflows,
        totalOutflows = totalOutflows,
        id = id,
        fileName = fileName,
    )
}

suspend fun HttpResponse.toStatementWithMetaData(): StatementWithMetaData {
    val start = headers["X-Statement-Start-Date"].orEmpty()
    val end = headers["X-Statement-End-Date"].orEmpty()
    val inflows = headers["X-Statement-Total-Inflows"]?.toDoubleOrNull() ?: 0.0
    val outflows = headers["X-Statement-Total-Outflows"]?.toDoubleOrNull() ?: 0.0

    return StatementWithMetaData(
        byteArray = readRawBytes(),
        startDate = parseLocalDateOrDefault(start),
        endDate = parseLocalDateOrDefault(end),
        totalInflows = inflows,
        totalOutflows = outflows,
    )
}
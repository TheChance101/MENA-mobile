@file:OptIn(ExperimentalTime::class)

package net.thechance.mena.wallet.data.mapper

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import net.thechance.mena.wallet.data.dto.local.LocalStatement
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.exceptions.UnknownNetworkException
import net.thechance.mena.wallet.domain.model.StatementWithMetaData
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

fun TransactionFilterParams.toStatementRequest(): HttpRequestBuilder.() -> Unit = {
    val timezone = TimeZone.currentSystemDefault()
    header(HttpHeaders.Accept, ContentType.Application.Pdf)
    types?.let { parameter("type", it.joinToString(",")) }

    startDate?.let {
        val startDateTime = it.atStartOfDayIn(timezone)
        parameter("from", startDateTime.toString().removeSuffix("Z"))
    }

    endDate?.let {
        val endDateTime = it.plus(DatePeriod(days = 1)).atStartOfDayIn(timezone)
        parameter("to", endDateTime.toString().removeSuffix("Z"))
    }
}

@OptIn(ExperimentalUuidApi::class)
fun Statement.toLocal(userId: String): LocalStatement {
    return LocalStatement(
        id = id.toString(),
        startDate = startDate.toString(),
        endDate = endDate.toString(),
        totalInflows = totalInflows,
        totalOutflows = totalOutflows,
        fileName = fileName,
        userId = userId
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
        id = id.toUuidOrNull()?: throw UnknownNetworkException("Invalid statement id"),
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
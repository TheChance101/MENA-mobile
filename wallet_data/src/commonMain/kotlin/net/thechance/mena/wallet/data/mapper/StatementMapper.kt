package net.thechance.mena.wallet.data.mapper

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.data.database.StatementEntity
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun TransactionFilterParams.toStatementRequest(): HttpRequestBuilder.() -> Unit = {
    header(HttpHeaders.Accept, ContentType.Application.Pdf)
    types?.let { parameter("type", it.joinToString(",")) }
    startDate?.let { parameter("startDate", it.toString()) }
    endDate?.let { parameter("endDate", it.toString()) }
}
@OptIn(ExperimentalUuidApi::class)
fun Statement.toDaoEntity(): StatementEntity {
    return StatementEntity(
        startDate=startDate.toString(),
        endDate=endDate.toString(),
        totalInflows=totalInflows,
        totalOutflows=totalOutflows,
        fileName = fileName
    )
}
@OptIn(ExperimentalUuidApi::class)
fun StatementEntity.toDomainEntity(): Statement {
    return Statement(
        startDate = LocalDate.parse(this.startDate),
        endDate = LocalDate.parse(this.endDate),
        totalInflows = totalInflows,
        totalOutflows = totalOutflows,
        id = Uuid.parse(this.id.toString()),
        fileName = fileName,
    )
}
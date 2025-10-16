package net.thechance.mena.wallet.data.mapper

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.data.database.LocalStatement
import net.thechance.mena.wallet.data.database.StatementWithMetaDataDto
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
        startDate=startDate.toString(),
        endDate=endDate.toString(),
        totalInflows=totalInflows,
        totalOutflows=totalOutflows,
        fileName = fileName
    )
}
@OptIn(ExperimentalUuidApi::class)
fun LocalStatement.toEntity(): Statement {
    return Statement(
        startDate = LocalDate.parse(this.startDate),
        endDate = LocalDate.parse(this.endDate),
        totalInflows = totalInflows,
        totalOutflows = totalOutflows,
        id = id,
        fileName = fileName,
    )
}
fun StatementWithMetaDataDto.toStatementWithMetaData(): StatementWithMetaData {
    return StatementWithMetaData(
        byteArray = this.byteArray,
            startDate = LocalDate.parse(this.startDate),
            endDate = LocalDate.parse(this.endDate),
            totalInflows = this.totalInflows,
            totalOutflows = this.totalOutflows,
    )
}
fun StatementWithMetaData.toStatementWithMetaDataDto(): StatementWithMetaDataDto {
    return StatementWithMetaDataDto(
        byteArray = this.byteArray,
        startDate = this.startDate.toString(),
        endDate = this.endDate.toString(),
        totalInflows = this.totalInflows,
        totalOutflows = this.totalOutflows,
    )
}


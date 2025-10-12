package net.thechance.mena.wallet.data.repository.statement.datasource.remote

import io.ktor.client.statement.HttpResponse
import net.thechance.mena.wallet.data.database.StatementEntity
import net.thechance.mena.wallet.domain.model.TransactionFilterParams

interface StatementRemoteDataSource {
    suspend fun getTransactionPdf(filterRequestParams: TransactionFilterParams?): ByteArray
    suspend fun getStatementFromTransactionResponse(filterRequestParams: TransactionFilterParams?): StatementEntity

}
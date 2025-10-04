package net.thechance.mena.wallet.data.repository.statement.datasource.remote

import net.thechance.mena.wallet.domain.model.TransactionFilterParams

interface StatementRemoteDataSource {
    suspend fun getTransactionPdf(filterRequestParams: TransactionFilterParams?): ByteArray
}
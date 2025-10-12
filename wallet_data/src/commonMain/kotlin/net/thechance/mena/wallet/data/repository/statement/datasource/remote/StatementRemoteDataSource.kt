package net.thechance.mena.wallet.data.repository.statement.datasource.remote

import net.thechance.mena.wallet.data.database.StatementWithMetaDataDto
import net.thechance.mena.wallet.domain.model.TransactionFilterParams

interface StatementRemoteDataSource {
    suspend fun getStatementWithMetaData(filterRequestParams: TransactionFilterParams?): StatementWithMetaDataDto
}
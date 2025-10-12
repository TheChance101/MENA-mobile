@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.domain.repository

import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.model.StatementWithMetaData
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import kotlin.uuid.ExperimentalUuidApi

interface StatementRepository {
    suspend fun getTransactionPdfWithMetaData(
        filterRequestParams: TransactionFilterParams? = null
    ): StatementWithMetaData

    suspend fun getStatements(
        page: Int,
        pageSize: Int,
    ): List<Statement>
    suspend fun insertStatement(statement: Statement)
    suspend fun deleteStatement(statement: Statement):Boolean
    suspend fun getStatementById(id:Long): Statement
    suspend fun insertStatementWithFileName(
        fileName: String,
        statement: Statement
    )

}
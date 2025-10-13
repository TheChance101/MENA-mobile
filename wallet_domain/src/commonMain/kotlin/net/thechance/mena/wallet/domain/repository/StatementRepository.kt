@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.domain.repository

import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.model.StatementWithMetaData
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import kotlin.uuid.ExperimentalUuidApi

interface StatementRepository {
    suspend fun getStatementWithMetadata(
        filterRequestParams: TransactionFilterParams? = null
    ): StatementWithMetaData

    suspend fun getStatements(
        page: Int,
        pageSize: Int,
    ): List<Statement>
    suspend fun insertStatement(statement: Statement)
    suspend fun deleteStatementById(id: Long)
    suspend fun getStatementById(id:Long): Statement

    suspend fun getCachedStatement(filterParams: TransactionFilterParams?=null): ByteArray?

}
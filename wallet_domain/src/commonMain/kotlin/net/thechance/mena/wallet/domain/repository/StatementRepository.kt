@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.domain.repository

import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.model.StatementWithMetaData
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface StatementRepository {
    suspend fun getStatementWithMetadata(filterRequestParams: TransactionFilterParams? = null): StatementWithMetaData
    suspend fun getStatements(page: Int, pageSize: Int): List<Statement>
    suspend fun insertStatement(statement: Statement)
    suspend fun deleteStatementById(id: Uuid)
    suspend fun getStatementById(id: Uuid): Statement
}
@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.domain.repository

import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface StatementRepository {
    suspend fun getTransactionsPdf(
        filterRequestParams: TransactionFilterParams? = null
    ): ByteArray

    suspend fun getStatements(
        page: Int,
        pageSize: Int,
    ): List<Statement> {
        return emptyList()
        /*TODO return all statements from local db*/
    }

    /**
     *we should delete statement from local db and return true if deleted successfully
     * something like  statementDao.deleteStatement(id)
     */

    suspend fun deleteStatement(id: Uuid): Boolean {
        return true
    }

}
@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.domain.repository

import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import kotlin.uuid.ExperimentalUuidApi

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
}
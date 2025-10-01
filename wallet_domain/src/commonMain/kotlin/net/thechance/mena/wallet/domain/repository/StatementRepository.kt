package net.thechance.mena.wallet.domain.repository

import net.thechance.mena.wallet.domain.model.TransactionFilterParams

interface StatementRepository {
    suspend fun getStatement(
        filterRequestParams: TransactionFilterParams? = null
    ): ByteArray
    suspend fun getLastStatement(): ByteArray?
}
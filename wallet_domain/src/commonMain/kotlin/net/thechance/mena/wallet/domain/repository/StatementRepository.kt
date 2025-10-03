package net.thechance.mena.wallet.domain.repository

import net.thechance.mena.wallet.domain.model.TransactionFilterParams

interface StatementRepository {
    suspend fun getTransactionsPdf(
        filterRequestParams: TransactionFilterParams? = null
    ): ByteArray
    suspend fun getStoredTransactionsPdf(): ByteArray?

}
package net.thechance.mena.wallet.domain.repository

import net.thechance.mena.wallet.domain.model.TransactionFilterParams

interface ExportTransactionsRepository {
    suspend fun getFilteredTransactionsFile(
        filterRequestParams: TransactionFilterParams? = null
    ): ByteArray
}
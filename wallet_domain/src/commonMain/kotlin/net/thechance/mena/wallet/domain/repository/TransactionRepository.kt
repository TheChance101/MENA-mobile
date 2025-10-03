package net.thechance.mena.wallet.domain.repository

import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface TransactionRepository {
    suspend fun getTransactionHistory(transactionFilterParams:TransactionFilterParams?): List<Transaction>
    suspend fun getTransactionById(transactionId: Uuid): Transaction
}
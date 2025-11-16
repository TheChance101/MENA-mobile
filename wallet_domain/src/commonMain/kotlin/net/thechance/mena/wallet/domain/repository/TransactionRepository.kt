package net.thechance.mena.wallet.domain.repository

import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface TransactionRepository {
    suspend fun getTransactionHistory(
        page: Int,
        pageSize: Int,
        transactionFilterParams: TransactionFilterParams?
    ): List<Transaction>

    suspend fun getTransactionById(transactionId: Uuid): Transaction
    suspend fun getFirstTransactionDate(): LocalDate?
    suspend fun addPendingTransaction(receiverId: Uuid, amount: Double): Uuid
    suspend fun submitTransaction(transactionId: Uuid)
}
package net.thechance.mena.wallet.domain.repository

import net.thechance.mena.wallet.domain.entity.Transaction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface TransactionRepository {
    suspend fun getTransactionById(transactionId: Uuid): Transaction
}
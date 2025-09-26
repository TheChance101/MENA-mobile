package net.thechance.mena.wallet.domain.repository

import net.thechance.mena.wallet.domain.entity.Transaction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface TransactionRepository {
    fun getTransactionDetails(transactionId: Uuid): Transaction
}
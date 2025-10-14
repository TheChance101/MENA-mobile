package net.thechance.mena.wallet.domain.repository

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface PaymentRepository {
    suspend fun submitTransaction(transactionId: Uuid)
}
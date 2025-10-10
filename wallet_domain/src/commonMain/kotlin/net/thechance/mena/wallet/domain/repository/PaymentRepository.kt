package net.thechance.mena.wallet.domain.repository

import net.thechance.mena.wallet.domain.model.PaymentConfirmation
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface PaymentRepository {
    suspend fun getPaymentConfirmation(receiverId: Uuid, amount: Double): PaymentConfirmation
}
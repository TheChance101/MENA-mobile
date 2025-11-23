package net.thechance.mena.dukan.domain.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Transaction(
    val transactionId: Uuid,
    val amount: Double
)

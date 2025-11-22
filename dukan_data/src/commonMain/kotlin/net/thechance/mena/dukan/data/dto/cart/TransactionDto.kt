package net.thechance.mena.dukan.data.dto.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class TransactionDto(
    @SerialName("transactionId")
    val transactionId: Uuid,
    @SerialName("totalAmount")
    val amount: Double
)
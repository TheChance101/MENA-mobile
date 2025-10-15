package net.thechance.mena.wallet.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PendingTransactionRequestBody(
    @SerialName("amount")
    val amount: Double,
    @SerialName("receiverId")
    val receiverId: String,
    @SerialName("type")
    val type: String
)
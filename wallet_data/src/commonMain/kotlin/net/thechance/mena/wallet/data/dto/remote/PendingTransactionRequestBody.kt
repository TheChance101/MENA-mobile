package net.thechance.mena.wallet.data.dto.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PendingTransactionRequestBody(
    @SerialName("amount")
    val amount: Double? = null,
    @SerialName("receiverId")
    val receiverId: String? = null,
)
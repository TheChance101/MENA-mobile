package net.thechance.mena.wallet.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentConfirmationRequest(
    @SerialName("receiverId")
    val receiverId: String,
    @SerialName("amount")
    val amount: Double
)

package net.thechance.mena.wallet.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentConfirmationDto(
    @SerialName("currentBalance")
    val currentBalance: Double? = null,
    @SerialName("isValid")
    val isValid: Boolean? = null,
    @SerialName("receiverImageUrl")
    val recipientImageUrl: String? = null,
    @SerialName("receiverName")
    val recipientName: String? = null
)
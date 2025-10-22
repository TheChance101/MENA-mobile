package net.thechance.mena.wallet.data.dto.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    @SerialName("id")
    val id: String,
    @SerialName("senderName")
    val senderName: String? = null,
    @SerialName("receiverName")
    val receiverName: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("amount")
    val amount: Double? = null
)

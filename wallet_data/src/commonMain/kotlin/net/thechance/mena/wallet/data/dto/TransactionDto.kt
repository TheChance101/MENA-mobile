package net.thechance.mena.wallet.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto (
    @SerialName("id")
    val id : String,
    @SerialName("senderName")
    val senderName: String?,
    @SerialName("receiverName")
    val receiverName: String?,
    @SerialName("status")
    val status: String?,
    @SerialName("type")
    val type: String?,
    @SerialName("createdAt")
    val createdAt: String?,
    @SerialName("amount")
    val amount: Double?
)

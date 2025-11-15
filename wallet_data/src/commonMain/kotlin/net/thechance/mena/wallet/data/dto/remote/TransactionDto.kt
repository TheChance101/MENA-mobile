package net.thechance.mena.wallet.data.dto.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    @SerialName("id")
    val id: String,
    @SerialName("sender")
    val senderInfo: TransactionPartyInfo? = TransactionPartyInfo(),
    @SerialName("receiver")
    val receiverInfo: TransactionPartyInfo? = TransactionPartyInfo(),
    @SerialName("status")
    val status: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("amount")
    val amount: Double? = null
)

@Serializable
data class TransactionPartyInfo(
    @SerialName("name")
    var name: String? = null,
    @SerialName("imageUrl")
    var imageUrl: String? = null
)

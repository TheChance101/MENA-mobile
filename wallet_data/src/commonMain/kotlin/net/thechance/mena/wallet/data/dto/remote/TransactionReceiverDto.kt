package net.thechance.mena.wallet.data.dto.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionReceiverDto(
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("name")
    val name: String? = null
)
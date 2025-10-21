package net.thechance.mena.wallet.data.dto.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BalanceDto(
    @SerialName("balance")
    val balance: Double? = null
)

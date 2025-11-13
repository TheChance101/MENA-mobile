package net.thechance.mena.admin_panel.data.remote.dto.deposit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DepositRequestDto(
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("amount")
    val amount: Double
)

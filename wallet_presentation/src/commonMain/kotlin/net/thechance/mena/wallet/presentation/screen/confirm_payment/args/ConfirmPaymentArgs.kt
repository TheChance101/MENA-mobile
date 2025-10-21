package net.thechance.mena.wallet.presentation.screen.confirm_payment.args

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmPaymentArgs(
    @SerialName("transactionId")
    val transactionId: String,
    @SerialName("amount")
    val amount: Double
)
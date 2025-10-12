package net.thechance.mena.wallet.presentation.screen.confirm_payment

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmPaymentArgs(
    val receiverId: String,
    val amount: Double
)
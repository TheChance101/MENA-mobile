package net.thechance.mena.wallet.presentation.screen.payment_result.args

import kotlinx.serialization.Serializable

@Serializable
data class PaymentResultArgs(
    val transactionId: String,
    val submitTransactionResultStatus: String,
    val receiverName: String,
    val amount: Double
)
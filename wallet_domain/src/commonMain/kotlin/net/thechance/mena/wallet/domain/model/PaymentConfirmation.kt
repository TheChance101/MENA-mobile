package net.thechance.mena.wallet.domain.model

data class PaymentConfirmation(
    val balance: Double,
    val receiverName: String,
    val receiverImg: String?,
    val status: Boolean
)
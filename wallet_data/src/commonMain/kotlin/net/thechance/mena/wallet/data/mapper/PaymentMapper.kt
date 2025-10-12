package net.thechance.mena.wallet.data.mapper

import net.thechance.mena.wallet.data.dto.PaymentConfirmationDto
import net.thechance.mena.wallet.domain.model.PaymentConfirmation
import net.thechance.mena.wallet.domain.model.TransactionStatus

fun PaymentConfirmationDto.toEntity() = PaymentConfirmation(
    balance = currentBalance?: 0.0,
    receiverName = recipientName ?: "",
    receiverImg = recipientImageUrl,
    status = isValid == true
)
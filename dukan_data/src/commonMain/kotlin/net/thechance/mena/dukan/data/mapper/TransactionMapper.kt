package net.thechance.mena.dukan.data.mapper

import net.thechance.mena.dukan.data.dto.cart.TransactionDto
import net.thechance.mena.dukan.domain.model.Transaction
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        transactionId = transactionId,
        amount = amount
    )
}
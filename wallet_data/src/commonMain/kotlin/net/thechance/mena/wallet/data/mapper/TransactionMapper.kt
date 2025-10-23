package net.thechance.mena.wallet.data.mapper

import net.thechance.mena.wallet.data.dto.remote.TransactionDto
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.entity.TransactionStatus
import net.thechance.mena.wallet.domain.entity.TransactionType
import net.thechance.mena.wallet.domain.exceptions.UnknownNetworkException
import kotlin.uuid.ExperimentalUuidApi

fun List<TransactionDto>?.toTransactionEntityList(): List<Transaction> {
    return this?.map { it.toEntity() }.orEmpty()
}

@OptIn(ExperimentalUuidApi::class)
fun TransactionDto.toEntity(): Transaction {
    return Transaction(
        id = id.toUuidOrNull()?: throw UnknownNetworkException("Invalid transaction id"),
        createdAt = parseLocalDateTimeOrDefault(createdAt),
        status = TransactionStatus.valueOfOrDefault(status),
        senderName = senderName ?: "",
        receiverName = receiverName ?: "",
        amount = amount ?: 0.0,
        type = TransactionType.valueOfOrDefault(type),
    )
}
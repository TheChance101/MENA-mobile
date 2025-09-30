package net.thechance.mena.wallet.data.mapper

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.wallet.data.dto.TransactionDto
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.model.TransactionStatus
import net.thechance.mena.wallet.domain.model.TransactionType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun TransactionDto.toEntity(): Transaction {
    return Transaction(
        id = Uuid.parse(id),
        createdAt = createdAt?.let { LocalDateTime.parse(it) } ?: LocalDateTime(2024, 1, 1, 0, 0),
        status = status?.let { TransactionStatus.valueOf(it) } ?: TransactionStatus.SUCCESS,
        senderName = senderName ?: "",
        receiverName = receiverName ?: "",
        amount = amount ?: 0.0,
        type = type?.let { TransactionType.valueOf(type) } ?: TransactionType.SENT,
    )

}

@OptIn(ExperimentalUuidApi::class)
fun Transaction.toDto(): TransactionDto {
    return TransactionDto(
        id = id.toString(),
        createdAt = createdAt.toString(),
        status = status.toString(),
        senderName = senderName,
        receiverName = receiverName,
        amount = amount,
        type = type.toString()
    )
}

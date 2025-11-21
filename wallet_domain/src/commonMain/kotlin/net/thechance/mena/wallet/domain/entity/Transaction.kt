package net.thechance.mena.wallet.domain.entity

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Transaction(
    val id: Uuid,
    val createdAt: LocalDateTime,
    val amount: Double,
    val status: TransactionStatus,
    val senderName: String,
    val senderImageUrl: String?,
    val receiverName: String,
    val receiverImageUrl: String?,
    val type: TransactionType
)

enum class TransactionType {
    SENT,
    RECEIVED,
    ONLINE_PURCHASE,
    DEPOSIT;

    companion object {
        fun valueOfOrDefault(value: String?): TransactionType {
            return runCatching { value?.let { TransactionType.valueOf(it) } ?: SENT }.getOrDefault(
                SENT
            )
        }
    }
}

enum class TransactionStatus {
    SUCCESS,
    FAILED;

    companion object {
        fun valueOfOrDefault(value: String?): TransactionStatus {
            return runCatching { value?.let { valueOf(it) } ?: SUCCESS }.getOrDefault(SUCCESS)
        }
    }
}
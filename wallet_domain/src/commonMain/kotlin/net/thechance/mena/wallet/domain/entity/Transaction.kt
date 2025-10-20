package net.thechance.mena.wallet.domain.entity

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Transaction (
    val id: Uuid,
    val createdAt: LocalDateTime,
    val amount: Double,
    val status: TransactionStatus,
    val senderName: String,
    val receiverName: String,
    val type: TransactionType
)

enum class TransactionType {
    SENT,
    RECEIVED,
    ONLINE_PURCHASE
}

enum class TransactionStatus {
    SUCCESS,
    FAILED
}
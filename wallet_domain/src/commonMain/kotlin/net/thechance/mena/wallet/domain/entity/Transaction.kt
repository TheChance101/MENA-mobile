package net.thechance.mena.wallet.domain.entity

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Transaction (
    val id: Uuid,
    val createdAt: LocalDateTime,
    val amount: Double,
    val status: Status,
    val senderId: Uuid,
    val senderName: String,
    val receiverId: Uuid,
    val receiverName: String,
    val type: Type
) {
    enum class Type {
        SENT,
        RECEIVED,
        ONLINE_PURCHASE
    }

    enum class Status {
        SUCCESS,
        FAIL
    }
}
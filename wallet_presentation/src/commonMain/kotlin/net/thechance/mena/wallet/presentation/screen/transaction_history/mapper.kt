package net.thechance.mena.wallet.presentation.screen.transaction_history

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.wallet.domain.entity.Transaction
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Transaction.toUi(): TransactionHistoryScreenState.TransactionHistoryUiState =
    TransactionHistoryScreenState.TransactionHistoryUiState(
        senderId = senderId,
        type = type,
        timeAndDate = formatTimeAndDate(createdAt),
        amount = amount.toString(),
        status = status,
        sender = if(type == Transaction.Type.SENT) senderName else null,
        receiver = if(type == Transaction.Type.RECEIVED) receiverName else null,
    )

private fun formatTimeAndDate(dateTime: LocalDateTime): String {
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    val month = dateTime.month.name.take(3).replaceFirstChar { it.uppercase() }
    val year = dateTime.year
    return "$hour:$minute $month $year"
}
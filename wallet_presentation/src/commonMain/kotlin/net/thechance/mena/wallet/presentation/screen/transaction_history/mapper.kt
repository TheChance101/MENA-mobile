package net.thechance.mena.wallet.presentation.screen.transaction_history

import kotlinx.datetime.LocalDateTime
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.from
import mena.wallet_presentation.generated.resources.to
import net.thechance.mena.wallet.domain.entity.Transaction
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Transaction.toUi(): TransactionHistoryScreenState.TransactionHistoryUiState =
    TransactionHistoryScreenState.TransactionHistoryUiState(
        id = id,
        timeAndDate = formatTimeAndDate(createdAt),
        amount = amount.toString(),
        type = when (type) {
            Transaction.Type.SENT -> TransactionHistoryScreenState.TransactionTypeUiState.SENT
            Transaction.Type.RECEIVED -> TransactionHistoryScreenState.TransactionTypeUiState.RECEIVED
            Transaction.Type.ONLINE_PURCHASE -> TransactionHistoryScreenState.TransactionTypeUiState.ONLINE_SHOPPING
        },
        status = when (status) {
            Transaction.Status.SUCCESS -> TransactionHistoryScreenState.TransactionStatusUiState.SUCCESS
            Transaction.Status.FAIL -> TransactionHistoryScreenState.TransactionStatusUiState.FAILED
        },
        userInfo = when (type) {
            Transaction.Type.SENT-> Res.string.from
            Transaction.Type.RECEIVED -> Res.string.to
            Transaction.Type.ONLINE_PURCHASE -> Res.string.from
        },
        contactName = if (type == Transaction.Type.SENT) senderName else receiverName
    )

private fun formatTimeAndDate(dateTime: LocalDateTime): String {
    val hour24 = dateTime.hour
    val minute = dateTime.minute.toString().padStart(2, '0')
    val amPm = if (hour24 < 12) "AM" else "PM"
    val hour12 = when {
        (hour24 == 0) -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }.toString()
    val month = dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    val day = dateTime.day
    return "$day $month, $hour12:$minute $amPm"
}
package net.thechance.mena.wallet.presentation.screen.transaction_history

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.model.TransactionStatus
import net.thechance.mena.wallet.domain.model.TransactionType
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Transaction.toUi(): TransactionHistoryScreenState.TransactionHistoryUiState =
    TransactionHistoryScreenState.TransactionHistoryUiState(
        id = id,
        timeAndDate = formatTimeAndDate(createdAt),
        amount = amount.toString(),
        type = when (type) {
            TransactionType.SENT -> TransactionHistoryScreenState.TransactionTypeUiState.SENT
            TransactionType.RECEIVED -> TransactionHistoryScreenState.TransactionTypeUiState.RECEIVED
            TransactionType.ONLINE_PURCHASE -> TransactionHistoryScreenState.TransactionTypeUiState.ONLINE_SHOPPING
        },
        status = when (status) {
            TransactionStatus.SUCCESS -> TransactionHistoryScreenState.TransactionStatusUiState.SUCCESS
            TransactionStatus.FAILED -> TransactionHistoryScreenState.TransactionStatusUiState.FAILED
        },
        contactName = when (type) {
            TransactionType.SENT -> receiverName
            TransactionType.RECEIVED -> senderName
            else -> null
        }
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

fun TransactionFilterState.toParams(): TransactionFilterParams {
    return TransactionFilterParams(
        types = selectedTypes.map { it.toDomainType() }.takeIf { it.isNotEmpty() },
        status = selectedStatus.toDomainStatus(),
        startDate = fromDate,
        endDate = toDate
    )
}

fun FilterType.toDomainType(): TransactionType = when (this) {
    FilterType.SENT -> TransactionType.SENT
    FilterType.RECEIVED -> TransactionType.RECEIVED
    FilterType.ONLINE_PURCHASE -> TransactionType.ONLINE_PURCHASE
}

fun FilterStatus.toDomainStatus(): TransactionStatus? = when (this) {
    FilterStatus.SUCCESS -> TransactionStatus.SUCCESS
    FilterStatus.FAILED -> TransactionStatus.FAILED
    FilterStatus.ALL -> null
}
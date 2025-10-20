package net.thechance.mena.wallet.presentation.screen.transaction_history

import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.model.TransactionStatus
import net.thechance.mena.wallet.domain.model.TransactionType
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.utils.formatLocalDateTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Transaction.toUi(): TransactionHistoryScreenState.TransactionHistoryUiState =
    TransactionHistoryScreenState.TransactionHistoryUiState(
        id = id,
        timeAndDate = formatLocalDateTime(date = createdAt, outputFormat = "dd MMM, h:mm a"),
        amount = amount.toString(),
        type = transactionType(),
        status = transactionStatus(),
        contactName =userName()
    )

fun TransactionFilterState.toParams(): TransactionFilterParams {
    return TransactionFilterParams(
        types = selectedTypes.map { it.toDomain() }.takeIf { it.isNotEmpty() },
        status = selectedStatus.toDomain(),
        startDate = startDate,
        endDate = endDate
    )
}

fun FilterType.toDomain(): TransactionType = when (this) {
    FilterType.SENT -> TransactionType.SENT
    FilterType.RECEIVED -> TransactionType.RECEIVED
    FilterType.ONLINE_PURCHASE -> TransactionType.ONLINE_PURCHASE
}
private fun Transaction.transactionType(): TransactionHistoryScreenState.TransactionTypeUiState = when (type) {
    TransactionType.SENT -> TransactionHistoryScreenState.TransactionTypeUiState.SENT
    TransactionType.RECEIVED -> TransactionHistoryScreenState.TransactionTypeUiState.RECEIVED
    TransactionType.ONLINE_PURCHASE -> TransactionHistoryScreenState.TransactionTypeUiState.ONLINE_SHOPPING
}

private fun Transaction.transactionStatus(): TransactionHistoryScreenState.TransactionStatusUiState = when (status) {
    TransactionStatus.SUCCESS -> TransactionHistoryScreenState.TransactionStatusUiState.SUCCESS
    TransactionStatus.FAILED -> TransactionHistoryScreenState.TransactionStatusUiState.FAILED
}

fun FilterStatus.toDomain(): TransactionStatus? = when (this) {
    FilterStatus.SUCCESS -> TransactionStatus.SUCCESS
    FilterStatus.FAILED -> TransactionStatus.FAILED
    FilterStatus.ALL -> null
}
private fun Transaction.userName(): String? = when (type) {
    TransactionType.SENT -> senderName
    TransactionType.RECEIVED -> receiverName
    else -> null
}
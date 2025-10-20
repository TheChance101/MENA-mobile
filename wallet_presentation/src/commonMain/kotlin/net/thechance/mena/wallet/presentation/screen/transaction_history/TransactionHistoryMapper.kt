package net.thechance.mena.wallet.presentation.screen.transaction_history

import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.entity.TransactionStatus
import net.thechance.mena.wallet.domain.entity.TransactionType
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
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

fun TransactionFilterState.toParams(): TransactionFilterParams {
    return TransactionFilterParams(
        types = selectedTypes.map { it.toDomainType() }.takeIf { it.isNotEmpty() },
        status = selectedStatus.toDomainStatus(),
        startDate = startDate,
        endDate = endDate
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
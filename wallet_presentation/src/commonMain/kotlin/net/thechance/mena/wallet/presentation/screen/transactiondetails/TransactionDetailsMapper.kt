@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transactiondetails

import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.presentation.utils.formateTransactionDate
import kotlin.uuid.ExperimentalUuidApi

fun Transaction.toUi() = TransactionDetailsScreenState.TransactionDetailsUiState(
    id = id.toString(),
    amount = amount.toString(),
    date = formateTransactionDate(createdAt),
    userName = when (type) {
        Transaction.Type.SENT -> senderName
        Transaction.Type.RECEIVED -> receiverName
        Transaction.Type.ONLINE_PURCHASE -> senderName
    },
    otherParty = when (type) {
        Transaction.Type.SENT -> receiverName
        Transaction.Type.RECEIVED -> senderName
        Transaction.Type.ONLINE_PURCHASE -> receiverName
    },
    transactionType = type,
    transactionStatus = status
)
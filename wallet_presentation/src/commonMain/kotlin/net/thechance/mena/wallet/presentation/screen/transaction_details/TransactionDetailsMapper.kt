@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transaction_details

import androidx.compose.ui.graphics.ImageBitmap
import io.github.suwasto.capturablecompose.CompressionFormat
import io.github.suwasto.capturablecompose.toByteArray
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.from
import mena.wallet_presentation.generated.resources.purchase
import mena.wallet_presentation.generated.resources.to
import mena.wallet_presentation.generated.resources.transfer
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.presentation.utils.formateTransactionDate
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionTypeUiState
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionStatusUiState
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
    transactionType = when (type) {
        Transaction.Type.SENT -> TransactionTypeUiState.SENT
        Transaction.Type.RECEIVED -> TransactionTypeUiState.RECEIVED
        Transaction.Type.ONLINE_PURCHASE -> TransactionTypeUiState.ONLINE_PURCHASE
    },
    transactionStatus = when (status) {
        Transaction.Status.SUCCESS -> TransactionStatusUiState.SUCCESS
        Transaction.Status.FAIL -> TransactionStatusUiState.FAILED
    },
    userInfo = when (type) {
        Transaction.Type.SENT, Transaction.Type.ONLINE_PURCHASE -> Res.string.from
        Transaction.Type.RECEIVED -> Res.string.to
    },
    typeContent = when (type) {
        Transaction.Type.SENT, Transaction.Type.RECEIVED -> Res.string.transfer
        Transaction.Type.ONLINE_PURCHASE -> Res.string.purchase
    },
    otherPartyTitle = when (type) {
        Transaction.Type.SENT, Transaction.Type.ONLINE_PURCHASE -> Res.string.to
        Transaction.Type.RECEIVED -> Res.string.from
    }
)

fun imageBitmapToByteArray(imageBitmap: ImageBitmap) =
    imageBitmap.toByteArray(CompressionFormat.PNG, 100)
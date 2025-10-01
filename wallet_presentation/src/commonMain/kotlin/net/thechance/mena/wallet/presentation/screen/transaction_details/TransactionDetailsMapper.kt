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
import net.thechance.mena.wallet.domain.model.TransactionStatus
import net.thechance.mena.wallet.domain.model.TransactionType
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionTypeUiState
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionStatusUiState
import net.thechance.mena.wallet.presentation.utils.formatTransactionDate
import kotlin.uuid.ExperimentalUuidApi

fun Transaction.toUi() = TransactionDetailsScreenState.TransactionDetailsUiState(
    id = id.toString(),
    amount = amount.toString(),
    date = formatTransactionDate(createdAt),
    userName = when (type) {
        TransactionType.SENT -> senderName
        TransactionType.RECEIVED -> receiverName
        TransactionType.ONLINE_PURCHASE -> senderName
    },
    otherParty = when (type) {
        TransactionType.SENT -> receiverName
        TransactionType.RECEIVED -> senderName
        TransactionType.ONLINE_PURCHASE -> receiverName
    },
    transactionType = when (type) {
        TransactionType.SENT -> TransactionTypeUiState.SENT
        TransactionType.RECEIVED -> TransactionTypeUiState.RECEIVED
        TransactionType.ONLINE_PURCHASE -> TransactionTypeUiState.ONLINE_PURCHASE
    },
    transactionStatus = when (status) {
        TransactionStatus.SUCCESS -> TransactionStatusUiState.SUCCESS
        TransactionStatus.FAIL -> TransactionStatusUiState.FAILED
    },
    userInfo = when (type) {
        TransactionType.SENT, TransactionType.ONLINE_PURCHASE -> Res.string.from
        TransactionType.RECEIVED -> Res.string.to
    },
    typeContent = when (type) {
        TransactionType.SENT, TransactionType.RECEIVED -> Res.string.transfer
        TransactionType.ONLINE_PURCHASE -> Res.string.purchase
    },
    otherPartyTitle = when (type) {
        TransactionType.SENT, TransactionType.ONLINE_PURCHASE -> Res.string.to
        TransactionType.RECEIVED -> Res.string.from
    }
)

fun imageBitmapToByteArray(imageBitmap: ImageBitmap) =
    imageBitmap.toByteArray(CompressionFormat.PNG, 100)
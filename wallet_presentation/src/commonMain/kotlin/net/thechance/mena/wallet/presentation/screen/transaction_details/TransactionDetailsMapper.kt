@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transaction_details

import androidx.compose.ui.graphics.ImageBitmap
import io.github.suwasto.capturablecompose.CompressionFormat
import io.github.suwasto.capturablecompose.toByteArray
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.from
import mena.wallet_presentation.generated.resources.purchase
import mena.wallet_presentation.generated.resources.receiver
import mena.wallet_presentation.generated.resources.to
import mena.wallet_presentation.generated.resources.transfer
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.entity.TransactionStatus
import net.thechance.mena.wallet.domain.entity.TransactionType
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionStatusUiState
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionTypeUiState
import net.thechance.mena.wallet.presentation.utils.formatAmount
import net.thechance.mena.wallet.presentation.utils.formatLocalDateTime
import kotlin.uuid.ExperimentalUuidApi

fun Transaction.toUi() = TransactionDetailsScreenState.TransactionDetailsUiState(
    id = getTransactionId(),
    amount = formatAmount(amount),
    date = getTransactionDate(),
    userName = getUserName(),
    otherParty = getOtherParty(),
    transactionType = getTransactionType(),
    transactionStatus = getTransactionStatus(),
    userInfo = getUserInfo(),
    typeContent = getTypeContent(),
    otherPartyTitle = getOtherPartyTitle()
)

fun imageBitmapToByteArray(imageBitmap: ImageBitmap): ByteArray =
    imageBitmap.toByteArray(CompressionFormat.PNG, quality = Constants.PNG_QUALITY)

private fun Transaction.getTransactionId(): String =
    Constants.ID_PREFIX + id.toString().substring(0, Constants.ID_LENGTH)

private fun Transaction.getTransactionDate(): String =
    formatLocalDateTime(date = createdAt, outputFormat = "dd MMM yyyy, h:mm a")

private fun Transaction.getUserName(): String = when (type) {
    TransactionType.SENT -> senderName
    TransactionType.RECEIVED -> receiverName
    TransactionType.ONLINE_PURCHASE -> senderName
    TransactionType.DEPOSIT -> receiverName
}

private fun Transaction.getOtherParty(): String = when (type) {
    TransactionType.SENT -> receiverName
    TransactionType.RECEIVED -> senderName
    TransactionType.ONLINE_PURCHASE -> receiverName
    TransactionType.DEPOSIT -> senderName
}

private fun Transaction.getTransactionType(): TransactionTypeUiState = when (type) {
    TransactionType.SENT -> TransactionTypeUiState.SENT
    TransactionType.RECEIVED -> TransactionTypeUiState.RECEIVED
    TransactionType.ONLINE_PURCHASE -> TransactionTypeUiState.ONLINE_PURCHASE
    TransactionType.DEPOSIT -> TransactionTypeUiState.DEPOSIT
}

private fun Transaction.getTransactionStatus(): TransactionStatusUiState = when (status) {
    TransactionStatus.SUCCESS -> TransactionStatusUiState.SUCCESS
    TransactionStatus.FAILED -> TransactionStatusUiState.FAILED
}

private fun Transaction.getUserInfo() = when (type) {
    TransactionType.SENT, TransactionType.ONLINE_PURCHASE -> Res.string.from
    TransactionType.RECEIVED -> Res.string.to
    TransactionType.DEPOSIT -> Res.string.to
}

private fun Transaction.getTypeContent() = when (type) {
    TransactionType.SENT, TransactionType.RECEIVED -> Res.string.transfer
    TransactionType.ONLINE_PURCHASE -> Res.string.purchase
    TransactionType.DEPOSIT ->Res.string.transfer
}

private fun Transaction.getOtherPartyTitle() = when (type) {
    TransactionType.SENT -> Res.string.to
    TransactionType.RECEIVED -> Res.string.from
    TransactionType.ONLINE_PURCHASE -> Res.string.receiver
    TransactionType.DEPOSIT -> Res.string.from
}

private object Constants {
    const val ID_PREFIX = "TX-"
    const val ID_LENGTH = 6
    const val PNG_QUALITY = 100
}